package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import sample.Observation;
import sample.State;
import sample.StepProbability;

import java.util.HashMap;

public class ModelDataController {

    @FXML
    private TextArea textBox;
    private SimulatorController simulatorController;

    public void init(SimulatorController simulatorController) {
        this.simulatorController = simulatorController;
        displayData();
    }

    private void displayData() {
        double[][] newIncidenceMatrix = new double[simulatorController.getSimulatorChain().size()][simulatorController.getSimulatorChain().size()];
        int max1 = 0, max2 = 0, max3 = -1;
        int maxSname = 0;

        for (StepProbability s : simulatorController.stepProbabilities) {
            if ((s.getLabel().getText().compareTo("NaN") == 0) || (s.getLabel().getTextFill().toString().compareTo("0x8b0000ff") == 0)) {
                max3 = -1;
                break;
            } else {
                if (Double.toString(s.getStepProbability()).length() > max3) {
                    max3 = Double.toString(s.getStepProbability()).length();
                }
            }
        }
        for (String key[] : simulatorController.transitionHashMap.keySet()) {
            int i = simulatorController.stateOrder.get(key[0]);
            int j = simulatorController.stateOrder.get(key[1]);
            newIncidenceMatrix[i][j] = simulatorController.transitionHashMap.get(key);
            if (max1 < Double.toString(newIncidenceMatrix[i][j]).length())
                max1 = Double.toString(newIncidenceMatrix[i][j]).length();

            if (max1 < Double.toString(simulatorController.transitionHashMap.get(key)).toString().length()) {
                max1 = Double.toString(simulatorController.transitionHashMap.get(key)).toString().length();

            }
            if (max1 < key[0].length()) {
                max1 = key[0].length();

            }
            if (maxSname < key[0].length()) {
                maxSname = key[0].length();
            }

            if (max1 < key[1].length()) {
                max1 = key[1].length();

            }
            if (maxSname < key[1].length()) {
                maxSname = key[1].length();
            }

        }

        for (State s : simulatorController.getSimulatorChain()) {
            if (maxSname > s.getStateName().getText().length()) {
                maxSname = s.getStateName().getText().length();
            }
        }
        double[][] observationMatrix = new double[simulatorController.getSimulatorChain().size()][simulatorController.simulatorObservations.size()];

        HashMap<String, Integer> obsHash = new HashMap<>();
        int i = 0;
        int j = 0;
        for (Observation o : simulatorController.simulatorObservations) {
            obsHash.put(o.getObservationName().getText(), i);
            i += 1;
        }
        for (String[] key : simulatorController.observationHashMap.keySet()) {
            i = simulatorController.stateOrder.get(key[1]);
            j = obsHash.get(key[0]);
            observationMatrix[i][j] = simulatorController.observationHashMap.get(key);
            if (max2 < Double.toString(observationMatrix[i][j]).length())
                max2 = Double.toString(observationMatrix[i][j]).length();

            if (max2 < Double.toString(simulatorController.observationHashMap.get(key)).toString().length()) {
                max2 = Double.toString(simulatorController.observationHashMap.get(key)).toString().length();

            }
        }
        String toShow = "";
        toShow = "State order:";
        for (State s : simulatorController.getSimulatorChain()) {
            toShow += s.getStateName().getText() + ",";
        }
        toShow = toShow.substring(0, toShow.length() - 1);
        toShow += "\n\n";

        if (max3 != -1) {
            toShow += "State distribution:\n";
            for (StepProbability s : simulatorController.stepProbabilities) {
                toShow += s.getStepProbability() + " ";
            }
            toShow += "\n\n";
        }

        for (State s : simulatorController.getSimulatorChain()) {
            if (s.getStateName().getText().length() > maxSname)
                maxSname = s.getStateName().getText().length();
        }


        toShow += "Transition matrix:\n";
        toShow = padRight(toShow, maxSname);
        toShow = padRight(toShow, 1);
        for (State s : simulatorController.getSimulatorChain()) {
            toShow += s.getStateName().getText();
            if (s.getStateName().getText().length() < max1) {
                toShow = padRight(toShow, max1 - s.getStateName().getText().length());
            }
            toShow += " ";
        }
        toShow += "\n";

        for (i = 0; i < simulatorController.getSimulatorChain().size(); i++) {
            toShow += simulatorController.getIncidenceMatrixChain().getStateOrderTree().get(i);
            if (simulatorController.getIncidenceMatrixChain().getStateOrderTree().get(i).length() < maxSname) {
                toShow = padRight(toShow, maxSname - simulatorController.getIncidenceMatrixChain().getStateOrderTree().get(i).length());
            }
            toShow = padRight(toShow, 1);
            for (j = 0; j < simulatorController.getSimulatorChain().size(); j++) {
                toShow += newIncidenceMatrix[i][j];
                if (Double.toString(newIncidenceMatrix[i][j]).length() < max1) {
                    toShow = padRight(toShow, max1 - Double.toString(newIncidenceMatrix[i][j]).length());
                }
                toShow += " ";

            }
            toShow = toShow.substring(0, toShow.length() - 1);
            toShow += "\n";
        }
        toShow += "\n";

        if (simulatorController.simulatorObservations.isEmpty() == false) {
            toShow += "Observations order:";
            for (Observation o : simulatorController.simulatorObservations) {
                toShow += o.getObservationName().getText() + ",";
            }
            toShow = toShow.substring(0, toShow.length() - 1);
            toShow += "\n\n";
            toShow += "Observations matrix:";
            toShow += "\n";

            max1 = 0;
            for (State s : simulatorController.getSimulatorChain()) {
                if (s.getStateName().getText().length() > max1)
                    max1 = s.getStateName().getText().length();
            }
            for (Observation o : simulatorController.simulatorObservations) {
                if (o.getObservationName().getText().length() > max1)
                    max1 = o.getObservationName().getText().length();
            }
            toShow = padRight(toShow, max1 + 1);
            for (Observation o : simulatorController.simulatorObservations) {

                toShow += o.getObservationName().getText();
                if (o.getObservationName().getText().length() < max2) {
                    toShow = padRight(toShow, max2 - o.getObservationName().getText().length());
                }
                toShow += " ";
            }


            toShow += "\n";

            for (i = 0; i < simulatorController.getSimulatorChain().size(); i++) {

                toShow += simulatorController.getIncidenceMatrixChain().getStateOrderTree().get(i);
                if (simulatorController.getIncidenceMatrixChain().getStateOrderTree().get(i).length() < max1) {
                    toShow = padRight(toShow, max1 - simulatorController.getIncidenceMatrixChain().getStateOrderTree().get(i).length());
                }
                toShow = padRight(toShow, 1);


                for (j = 0; j < simulatorController.simulatorObservations.size(); j++) {

                    toShow += observationMatrix[i][j];
                    if (Double.toString(observationMatrix[i][j]).length() < max1) {
                        toShow = padRight(toShow, max1 - Double.toString(observationMatrix[i][j]).length());
                    }
                    toShow += " ";
                }
                toShow = toShow.substring(0, toShow.length() - 1);
                toShow += "\n";
            }
        }
        textBox.setText(toShow);


    }

    public String padRight(String s, int n) {
        for (int i = 0; i < n; i++) {
            s += " ";
        }
        return s;
    }

}
