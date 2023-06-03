package controller;

import model.ModelSave;

import java.util.Set;

public class ControllerSave {

    private final ModelSave modelSave;

    public ControllerSave() {

        this.modelSave = new ModelSave();

    }

    public void save(Set<Integer> saveData) {
        modelSave.save(saveData);
    }

}
