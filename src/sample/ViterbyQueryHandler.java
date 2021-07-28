package sample;

import controllers.SimulatorController;
import controllers.ViterbiQueryController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ViterbyQueryHandler {

    public void openViterbiQuery(SimulatorController simulatorController) {
        try {

            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/viterbiQuery.fxml"));
            Parent root = fxmlLoader.load();
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Viterbi Query");
            stage.setScene(new Scene(root));
            ViterbiQueryController viterbiQueryController = fxmlLoader.getController();
            viterbiQueryController.setSimulatorController(simulatorController);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
