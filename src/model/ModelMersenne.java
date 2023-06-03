package model;

import controller.ControllerMersenne;

import java.math.BigInteger;

public class ModelMersenne implements Runnable {

    private static final BigInteger THREE = BigInteger.valueOf(3);
    private static final BigInteger SIX = BigInteger.valueOf(6);

    private final int initialExponent;
    private final int exponentOffset;

    public ModelMersenne(int initialExponent, int exponentOffset) {
        this.initialExponent = initialExponent;
        this.exponentOffset = exponentOffset;
    }

    private boolean isPrime(BigInteger n) {

        if(n.compareTo(BigInteger.TWO) < 0) return false;
        if(n.compareTo(BigInteger.TWO) == 0 || n.compareTo(THREE) == 0) return true;
        if (n.mod(BigInteger.TWO).compareTo(BigInteger.ZERO) == 0 || n.mod(THREE).compareTo(BigInteger.ZERO) == 0) return false;

        BigInteger nSqrt = n.sqrt().subtract(BigInteger.ONE);

        for (BigInteger i = SIX; i.compareTo(nSqrt) <= 0; i = i.add(SIX)) {
            if (ModelFlowControl.latchFindMersenne.getCount() == 0) break;
            if (n.mod(i.subtract(BigInteger.ONE)).compareTo(BigInteger.ZERO) == 0 || n.mod(i.add(BigInteger.ONE)).compareTo(BigInteger.ZERO) == 0) return false;
        }

        return true;

    }

    @Override
    public void run() {

        int exponent = initialExponent;

        BigInteger currentNumber;

        while (ModelFlowControl.latchFindMersenne.getCount() > 0) {

            if (exponent % 2 != 0) {

                currentNumber = BigInteger.TWO.pow(exponent);
                currentNumber = currentNumber.subtract(BigInteger.ONE);

                if (isPrime(currentNumber)) ControllerMersenne.resultSet.add(exponent);

            }

            exponent += exponentOffset;

        }

        ModelFlowControl.latchModelMersenne.countDown();

    }

}
