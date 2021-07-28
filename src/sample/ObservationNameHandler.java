package sample;

import controllers.SetObservationNameController;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class ObservationNameHandler {

    public static int instanceNumber = 0;

    public void enableObservationNameOnClick(Observation observation) {

        observation.getObservationName().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (instanceNumber == 0)
                    try {
                        instanceNumber += 1;
                        Stage stage = new Stage();
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/setObservationName.fxml"));
                        Parent root = fxmlLoader.load();
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.initModality(Modality.WINDOW_MODAL);
                        stage.setResizable(false);
                        stage.setTitle("Set observation name");
                        stage.setScene(new Scene(root));
                        Bounds boundsInScreen = observation.getObservationName().localToScreen(observation.getObservationName().getBoundsInLocal());
                        stage.setX(boundsInScreen.getMaxX());
                        stage.setY(boundsInScreen.getMaxY());
                        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                            @Override
                            public void handle(WindowEvent event) {
                                instanceNumber -= 1;
                            }
                        });
                        SetObservationNameController setObservationNameController = fxmlLoader.getController();
                        setObservationNameController.initObservation(observation);
                        stage.show();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

            }
        });

    }

    public void disableObservationNameOnClick(Observation observation) {
        observation.getObservationName().setOnMouseClicked(null);

    }


}
