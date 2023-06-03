package controller;

import model.ModelFlowControl;
import model.ModelMersenne;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControllerMersenne implements Runnable {

    private final int threadCount;
    private final ModelMersenne[] modelMersenneArr;
    public static ConcurrentSkipListSet<Integer> resultSet;

    public ControllerMersenne(int threadCount) {

        this.threadCount = threadCount;
        modelMersenneArr = new ModelMersenne[threadCount];
        resultSet = new ConcurrentSkipListSet<>();

    }

    @Override
    public void run() {

        ModelFlowControl.latchFindMersenne = new CountDownLatch(1);
        ModelFlowControl.latchModelMersenne = new CountDownLatch(threadCount);

        ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);

        int initialExponent = 1;

        resultSet.add(2);

        for (int i = 0; i < threadCount; i++) {
            modelMersenneArr[i] = new ModelMersenne(initialExponent, threadCount * 2);
            initialExponent += 2;
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
