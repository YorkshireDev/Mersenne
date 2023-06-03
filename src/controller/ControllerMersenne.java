package controller;

import model.ModelFlowControl;

public class ControllerMersenne implements Runnable {

    @Override
    public void run() {

        while (ModelFlowControl.latchViewMain.getCount() > 0) {
            Thread.onSpinWait();
        }

        ModelFlowControl.latchControllerMersenne.countDown();

    }

}
