package sample;

import controllers.SetStateNameController;
import controllers.SimulatorController;
import controllers.TableViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.math.BigDecimal;

public class TableViewHandler {

    public void displayTable(BigDecimal[][] probabilityMatrix, String[] query, int mode, SimulatorController simulatorController) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/tableView.fxml"));
            Parent root = fxmlLoader.load();
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.WINDOW_MODAL);
            if (mode == 0)
                stage.setTitle("Estimation");
            if (mode == 1)
                stage.setTitle("Decoding");
            if (mode == 2)
                stage.setTitle("Forward-Backward algorithm");
            if (mode == 3)
                stage.setTitle("Backward algorithm");
            stage.setScene(new Scene(root));
            TableViewController tableViewController = fxmlLoader.getController();
            tableViewController.setTable(probabilityMatrix, query, mode, simulatorController);
            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void displayTable(String[] query,  SimulatorController simulatorController) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/tableView.fxml"));
            Parent root = fxmlLoader.load();
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle("State sequence probability");
            stage.setScene(new Scene(root));
            TableViewController tableViewController = fxmlLoader.getController();
            tableViewController.setTable(query, simulatorController);
            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
