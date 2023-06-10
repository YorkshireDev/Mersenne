package controller;

import model.ModelFlowControl;
import model.ModelMersenne;

import java.util.concurrent.*;

public class ControllerMersenne implements Runnable {

    private int initialExponent;
    private final int threadCount;
    private final ModelMersenne[] modelMersenneArr;
    public static ConcurrentSkipListSet<Integer> resultSet;

    long eProcessedTotal;
    double eProcessedPerTime;
    long eTimeUnit;
    long eLargestProcessed;

    public ControllerMersenne(int initialExponent, int threadCount) {

        this.initialExponent = initialExponent;
        this.threadCount = threadCount;
        modelMersenneArr = new ModelMersenne[threadCount];
        resultSet = new ConcurrentSkipListSet<>();
        eProcessedTotal = 0L;
        eProcessedPerTime = 0L;
        eTimeUnit = 0L; // 0 = Second, 1 = Minute, 2 = Hour
        eLargestProcessed = 0L;

    }

    public double[] getStatistics() {
        return new double[] { eProcessedTotal, eProcessedPerTime, eTimeUnit, eLargestProcessed };
    }

    @Override
    public void run() {

        ModelFlowControl.latchFindMersenne = new CountDownLatch(1);
        ModelFlowControl.latchModelMersenne = new CountDownLatch(threadCount);

        ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);

        if (initialExponent < 3) {
            initialExponent = 3;
            resultSet.add(2);
        }

        double sTime;
        double eTime;

        sTime = System.nanoTime();

        for (int i = 0; i < threadCount; i++) {
            modelMersenneArr[i] = new ModelMersenne(initialExponent, threadCount * 2);
            initialExponent += 2;
            threadPool.submit(modelMersenneArr[i]);
        }

        threadPool.shutdown();

        try {
            ModelFlowControl.latchViewMain.await(); // Wait for signal to stop...
            ModelFlowControl.latchFindMersenne.countDown(); // Tell the mersenne finders to stop...
            ModelFlowControl.latchModelMersenne.await(8, TimeUnit.SECONDS); // Wait for them to stop...
        } catch (InterruptedException e) { throw new RuntimeException(e); }

        eTime = System.nanoTime() - sTime;
        eTime /= 1_000_000_000.0d;

        if (eTime <= 0) eTime = 1L;

        for (ModelMersenne modelMersenne : modelMersenneArr) {
            eProcessedTotal += modelMersenne.getEProcessed();
            if (modelMersenne.getLargestEProcessed() > eLargestProcessed) eLargestProcessed = modelMersenne.getLargestEProcessed();
        }

        eProcessedPerTime = (double) eProcessedTotal / eTime;

        if (eProcessedPerTime < 1.0d) {
            eProcessedPerTime *= 60.0d;
            eTimeUnit = 1L;
        }

        if (eProcessedPerTime < 1.0d) {
            eProcessedPerTime *= 60.0d;
            eTimeUnit = 2L;
        }

        ModelFlowControl.latchControllerMersenne.countDown(); // Tell the signaler that you've finished...

    }

}
