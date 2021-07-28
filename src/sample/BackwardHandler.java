package sample;

import controllers.BackwardQueryController;
import controllers.SimulatorController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class BackwardHandler {
    public void openBackwardQuery(SimulatorController simulatorController) {
        try {

            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/backwardQuery.fxml"));
            Parent root = fxmlLoader.load();
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Backward Query");
            stage.setScene(new Scene(root));
            BackwardQueryController backwardQueryController = fxmlLoader.getController();
            backwardQueryController.setSimulatorController(simulatorController);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
