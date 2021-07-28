package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class StateSequenceProbabilityController {
    @FXML
    private TextArea viterbiQueryText;


    private SimulatorController simulatorController;

    public void setSimulatorController(SimulatorController simulatorController) {
        this.simulatorController = simulatorController;
    }


    public void tryQuery(ActionEvent actionEvent) {
        String text = viterbiQueryText.getText();
        String[] query = text.split(",");
        boolean cont = true;
        for (String st : query) {
            if (simulatorController.stateOrder.containsKey(st) == false) {
                cont = false;
            }
        }
        if (cont) {
            simulatorController.displayStateSequenceQuery(query);
            Stage st = (Stage) viterbiQueryText.getScene().getWindow();
            st.close();
        }

    }
}
