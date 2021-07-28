package sample;

import controllers.ModelDataController;
import controllers.SimulatorController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ModelDataHandler {
    public void openModelHandler(SimulatorController simulatorController) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/modelData.fxml"));
            Parent root = fxmlLoader.load();
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root));
            ModelDataController modelDataController = fxmlLoader.getController();
            modelDataController.init(simulatorController);
            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
