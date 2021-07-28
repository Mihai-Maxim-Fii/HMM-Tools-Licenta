package sample;

import controllers.SimulatorController;
import controllers.StateSequenceProbabilityController;
import controllers.StationaryDistributionController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;

public class StateSequenceProbabilityHandler {
    public void displayStateSequenceProbability(SimulatorController simulatorController) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/stateSequenceQuery.fxml"));
            Parent root = fxmlLoader.load();
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle("State sequence probability");
            stage.setScene(new Scene(root));
            StateSequenceProbabilityController stateSequenceProbabilityController = fxmlLoader.getController();
            stateSequenceProbabilityController.setSimulatorController(simulatorController);
            stage.show();


        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}
