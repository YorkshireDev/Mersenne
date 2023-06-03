package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ModelMersenne implements Runnable {

    private final List<String> resultList;

    public ModelMersenne() {
        resultList = new ArrayList<>();
    }

    public List<String> getResultList() {
        List<String> resultListTemp = new ArrayList<>(resultList);
        resultList.clear();
        return resultListTemp;
    }

    @Override
    public void run() {

        while (ModelFlowControl.latchFindMersenne.getCount() > 0) {
            try {
                Thread.sleep(1024L);
                resultList.add(String.valueOf(ThreadLocalRandom.current().nextInt()));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        ModelFlowControl.latchModelMersenne.countDown();

    }

}
