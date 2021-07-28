package sample;

import controllers.MainController;
import controllers.SimulatorController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;

public class SimulatorHandler extends MainController {


    public void openInSimulator(List<State> chain, IncidenceMatrixChain incidenceMatrixChain) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/simulator.fxml"));
            Parent root = fxmlLoader.load();
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle("Simulator");
            stage.setScene(new Scene(root));

            SimulatorController simulatorController = fxmlLoader.getController();
            simulatorController.setIncidenceMatrixChain(incidenceMatrixChain);
            simulatorController.setSimulatorChain(chain);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
