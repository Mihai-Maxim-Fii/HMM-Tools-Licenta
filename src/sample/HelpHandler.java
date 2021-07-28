package sample;

import controllers.HelpController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class HelpHandler {
    public void openHelp(int textNo) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/help.fxml"));
            Parent root = fxmlLoader.load();
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(true);
            if (textNo == 2)
                stage.setTitle("Simulator features");
            if (textNo == 1)
                stage.setTitle("Drawing features");
            if (textNo == 3)
                stage.setTitle("Other features");

            stage.setScene(new Scene(root));
            HelpController helpController = fxmlLoader.getController();
            helpController.setText(textNo);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
