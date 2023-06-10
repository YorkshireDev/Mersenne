package model;

import controller.ControllerMersenne;

import java.math.BigInteger;

public class ModelMersenne implements Runnable {

    private static final BigInteger FOUR = BigInteger.valueOf(4);

    private final int initialExponent;
    private final int exponentOffset;

    private int exponent;

    private long eProcessed;

    public ModelMersenne(int initialExponent, int exponentOffset) {
        this.initialExponent = initialExponent;
        this.exponentOffset = exponentOffset;
        exponent = 0;
        eProcessed = 0L;
    }

    public long getEProcessed() {
        return eProcessed;
    }

    public int getLargestEProcessed() {
        return exponent;
    }

    private boolean isPrime(int exponent, BigInteger nCheck) {

        BigInteger sNumber = FOUR.mod(nCheck);

        for (int i = 1; i < exponent - 1; i++) {
            if (ModelFlowControl.latchFindMersenne.getCount() == 0) break;
            sNumber = (sNumber.multiply(sNumber).subtract(BigInteger.TWO)).mod(nCheck);
        }
        
        return sNumber.compareTo(BigInteger.ZERO) == 0;

    }

    @Override
    public void run() {

        exponent = initialExponent;

        BigInteger currentNumber;

        while (ModelFlowControl.latchFindMersenne.getCount() > 0) {

            currentNumber = BigInteger.TWO.pow(exponent).subtract(BigInteger.ONE);

            if (isPrime(exponent, currentNumber)) ControllerMersenne.resultSet.add(exponent);

            if (ModelFlowControl.latchFindMersenne.getCount() == 0) continue;

            exponent += exponentOffset;
            eProcessed++;

        }

        ModelFlowControl.latchModelMersenne.countDown();

    }

}
