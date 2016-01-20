package jmri.jmrit.operations.automation.actions;

import jmri.jmrit.operations.trains.Train;
import jmri.jmrit.operations.trains.TrainCustomManifest;

public class RunTrainAction extends Action {

    private static final int _code = ActionCodes.RUN_TRAIN;

    @Override
    public int getCode() {
        return _code;
    }

    @Override
    public String getName() {
           return Bundle.getMessage("RunTrain");
    }

    @Override
    public void doAction() {
        if (getAutomationItem() != null) {
            Train train = getAutomationItem().getTrain();
            if (train != null && train.isBuilt() && TrainCustomManifest.manifestCreatorFileExists()) {
                setRunning(true);
                TrainCustomManifest.addCVSFile(train.createCSVManifestFile());
                TrainCustomManifest.process();
                finishAction(true);
            } else {
                finishAction(false);
            }
        }
    }

    @Override
    public void cancelAction() {
        // no cancel for this action     
    }

}
