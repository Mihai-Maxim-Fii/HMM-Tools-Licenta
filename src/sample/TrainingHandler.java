package sample;

import controllers.SimulatorController;
import controllers.TrainingController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class TrainingHandler {

    public void openTraining(SimulatorController simulatorController) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/trainingQuery.fxml"));
            Parent root = fxmlLoader.load();
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Training");
            stage.setScene(new Scene(root));
            TrainingController trainingController = fxmlLoader.getController();
            trainingController.setSimulatorController(simulatorController);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
