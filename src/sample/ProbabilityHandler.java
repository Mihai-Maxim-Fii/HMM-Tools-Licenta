package sample;

import controllers.SetProbabilityController;
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

public class ProbabilityHandler {
    public static int instanceNumber = 0;


    public void enableOnClickProbability(Transition transition) {

        transition.getCurve().getProbabilityLabel().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                if (instanceNumber == 0)
                    try {
                        Stage stage = new Stage();
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/setProbability.fxml"));
                        Parent root = fxmlLoader.load();
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.initModality(Modality.WINDOW_MODAL);
                        stage.setResizable(false);
                        stage.setTitle("Set Probability");
                        stage.setScene(new Scene(root));
                        Bounds boundsInScreen = transition.getCurve().getProbabilityLabel().localToScreen(transition.getCurve().getProbabilityLabel().getBoundsInLocal());
                        stage.setX(boundsInScreen.getMaxX());
                        stage.setY(boundsInScreen.getMaxY());

                        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                            @Override
                            public void handle(WindowEvent event) {
                                instanceNumber -= 1;
                            }
                        });
                        SetProbabilityController setProbabilityController = fxmlLoader.getController();
                        setProbabilityController.setTransition(transition);
                        stage.show();
                        instanceNumber += 1;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

            }
        });

    }

    public void disableOnClickProbability(Transition transition) {
        transition.getCurve().getProbabilityLabel().setOnMouseClicked(null);
    }
}
