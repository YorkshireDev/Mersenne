package controller;

import model.ModelFlowControl;
import model.ModelMersenne;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControllerMersenne implements Runnable {

    private final int threadCount;
    private final ModelMersenne[] modelMersenneArr;

    public ControllerMersenne(int threadCount) {

        this.threadCount = threadCount;
        modelMersenneArr = new ModelMersenne[threadCount];

    }

    public List<String> getResultList() {

        NavigableSet<String> resultList = new TreeSet<>();

        for (ModelMersenne modelMersenne : modelMersenneArr) {
            if (modelMersenne == null) continue;
            resultList.addAll(modelMersenne.getResultList());
        }

        return new ArrayList<>(resultList);

    }

    @Override
    public void run() {

        ModelFlowControl.latchFindMersenne = new CountDownLatch(1);
        ModelFlowControl.latchModelMersenne = new CountDownLatch(threadCount);

        ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            modelMersenneArr[i] = new ModelMersenne();
            threadPool.submit(modelMersenneArr[i]);
        }

        try {
            ModelFlowControl.latchViewMain.await(); // Wait for signal to stop...
            ModelFlowControl.latchFindMersenne.countDown(); // Tell the mersenne finders to stop...
            ModelFlowControl.latchModelMersenne.await(); // Wait for them to stop...
        } catch (InterruptedException e) { throw new RuntimeException(e); }

        ModelFlowControl.latchControllerMersenne.countDown(); // Tell the signaler that you've finished...

    }

}
