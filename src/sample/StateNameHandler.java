package sample;

import controllers.SetStateNameController;
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
import java.util.List;

public class StateNameHandler {

    public static int instanceNumber = 0;

    public void enableStateNameOnClick(State state, List<State> currentStates) {
        state.getStateName().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                if (instanceNumber == 0)
                    try {
                        instanceNumber += 1;
                        Stage stage = new Stage();
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/setStateName.fxml"));
                        Parent root = fxmlLoader.load();
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.initModality(Modality.WINDOW_MODAL);
                        stage.setResizable(false);
                        stage.setTitle("Set Name");
                        Bounds boundsInScreen = state.getStateName().localToScreen(state.getStateName().getBoundsInLocal());
                        stage.setX(boundsInScreen.getMaxX());
                        stage.setY(boundsInScreen.getMaxY());
                        stage.setScene(new Scene(root));
                        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                            @Override
                            public void handle(WindowEvent event) {
                                instanceNumber -= 1;
                            }
                        });
                        SetStateNameController setStateNameController = fxmlLoader.getController();
                        setStateNameController.setState(state);
                        setStateNameController.setCurrentStates(currentStates);
                        stage.show();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

            }
        });

    }

    public void disableStateNameOnClick(State state, List<State> currentStates) {
        state.getStateName().setOnMouseClicked(null);
    }
}
