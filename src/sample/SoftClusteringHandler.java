package sample;

import controllers.EstimationQueryController;
import controllers.SimulatorController;
import controllers.SoftClusteringController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class SoftClusteringHandler {
    public void openSoftClusteringQuery(SimulatorController simulatorController) {
        try {

            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/softClustering.fxml"));
            Parent root = fxmlLoader.load();
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Forward-Backward algorithm");
            stage.setScene(new Scene(root));
            SoftClusteringController softClusteringController = fxmlLoader.getController();
            softClusteringController.setSimulatorController(simulatorController);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
