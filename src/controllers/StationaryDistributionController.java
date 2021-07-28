package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import sample.State;

import java.math.BigDecimal;
import java.util.List;

public class StationaryDistributionController {

    @FXML
    private TextArea textBox;
    private SimulatorController simulatorController;
    private List<double[]> stationaryDistribution;

    public void init(SimulatorController simulatorController, List<double[]> stationaryDistribution) {
        this.simulatorController = simulatorController;
        this.stationaryDistribution = stationaryDistribution;
        displayStationaryDistribution();
    }

    public void displayStationaryDistribution() {
        int max = 0;
        for (State s : simulatorController.getSimulatorChain()) {
            if (s.getStateName().getText().length() > max) {
                max = s.getStateName().getText().length();
            }
        }

        for (double[] dist : stationaryDistribution) {
            for (int i = 0; i < simulatorController.getSimulatorChain().size(); i++) {
                if (BigDecimal.valueOf(dist[i]).toString().length() > max)
                    max = BigDecimal.valueOf(dist[i]).toString().length();
            }
        }


        String toShow = "";
        for (State s : simulatorController.getSimulatorChain()) {

            toShow += s.getStateName().getText();
            if (s.getStateName().getText().length() < max) {
                toShow = padRight(toShow, max - s.getStateName().getText().length());
            }
            toShow = padRight(toShow, 2);
        }
        toShow += "\n";

        for (double[] dist : stationaryDistribution) {
            for (int i = 0; i < simulatorController.getSimulatorChain().size(); i++) {
                String number = BigDecimal.valueOf(dist[i]).toString();
                toShow += number;
                if (number.length() < max)
                    toShow = padRight(toShow, max - number.length());
                toShow = padRight(toShow, 2);
            }
            toShow += "\n";
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
