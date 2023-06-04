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

    long eProcessedTotal;
    long eProcessedPerSecond;
    long eLargestProcessed;

    public ControllerMersenne(int threadCount) {

        this.threadCount = threadCount;
        modelMersenneArr = new ModelMersenne[threadCount];
        resultSet = new ConcurrentSkipListSet<>();
        eProcessedTotal = 0L;
        eProcessedPerSecond = 0L;
        eLargestProcessed = 0L;

    }

    public long[] getStatistics() {
        return new long[] {eProcessedTotal, eProcessedPerSecond, eLargestProcessed};
    }

    @Override
    public void run() {

        ModelFlowControl.latchFindMersenne = new CountDownLatch(1);
        ModelFlowControl.latchModelMersenne = new CountDownLatch(threadCount);

        ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);

        int initialExponent = 1;

        resultSet.add(2);

        long sTime;
        long eTime;

        sTime = System.nanoTime();

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

        eTime = System.nanoTime() - sTime;
        eTime /= 1_000_000_000L;

        if (eTime <= 0) eTime = 1L;

        for (ModelMersenne modelMersenne : modelMersenneArr) {
            eProcessedTotal += modelMersenne.getEProcessed();
            if (modelMersenne.getLargestEProcessed() > eLargestProcessed) eLargestProcessed = modelMersenne.getLargestEProcessed();
        }

        eProcessedPerSecond = eProcessedTotal / eTime;

        ModelFlowControl.latchControllerMersenne.countDown(); // Tell the signaler that you've finished...

    }

}
