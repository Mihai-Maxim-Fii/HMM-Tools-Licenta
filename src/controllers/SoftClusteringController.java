package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class SoftClusteringController {
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
            if (simulatorController.obsNameSet.contains(st) == false) {
                cont = false;
            }
        }
        if (cont) {
            simulatorController.forwardBackward(text, true);
            Stage st = (Stage) viterbiQueryText.getScene().getWindow();
            st.close();
        }

    }
}
