package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class TrainingController {
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
            simulatorController.trainingData = text;
            simulatorController.separator.setVisible(true);
            simulatorController.trainButton.setVisible(true);
            simulatorController.roundsCounter.setVisible(true);
            Stage st = (Stage) viterbiQueryText.getScene().getWindow();
            st.close();
        }
    }


}
