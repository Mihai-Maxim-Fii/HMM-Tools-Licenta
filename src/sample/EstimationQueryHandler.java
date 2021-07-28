package sample;

import controllers.EstimationQueryController;
import controllers.SimulatorController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class EstimationQueryHandler {
    public void openEstimationQuery(SimulatorController simulatorController) {
        try {

            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/estimationQuery.fxml"));
            Parent root = fxmlLoader.load();
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Estimation Query");
            stage.setScene(new Scene(root));
            EstimationQueryController estimationQueryController = fxmlLoader.getController();
            estimationQueryController.setSimulatorController(simulatorController);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
