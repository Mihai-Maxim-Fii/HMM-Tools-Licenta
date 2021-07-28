package sample;

import controllers.SimulatorController;
import controllers.StationaryDistributionController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;

public class StationaryDistributionHandler {

    public void displayStationaryDistribution(SimulatorController simulatorController, List<double[]> stationaryDistribution) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/stationaryDistribution.fxml"));
            Parent root = fxmlLoader.load();
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle("Stationary Distribution");
            stage.setScene(new Scene(root));
            StationaryDistributionController stationaryDistributionController = fxmlLoader.getController();
            stationaryDistributionController.init(simulatorController, stationaryDistribution);
            stage.show();


        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

}
