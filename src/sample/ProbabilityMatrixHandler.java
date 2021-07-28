package sample;

import controllers.ProbabilityMatrixController;
import controllers.SetStateNameController;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.List;

public class ProbabilityMatrixHandler {

    public static int instanceNumber = 0;

    public void openProbabilityChainMatrix(Chain currentChain, Pane mainDrawingPane, List<State> currentlySelectedStates, List<Transition> currentlySelectedTransitions) {
        if (instanceNumber == 0)
            try {
                instanceNumber += 1;
                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/probabilityMatrix.fxml"));
                Parent root = fxmlLoader.load();
                stage.initStyle(StageStyle.DECORATED);
                stage.initModality(Modality.WINDOW_MODAL);
                stage.setResizable(false);
                stage.setTitle("Enter Probability Matrix");
                stage.setScene(new Scene(root));
                stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        instanceNumber -= 1;
                    }
                });
                ProbabilityMatrixController setProbabilityMatrixController = fxmlLoader.getController();
                setProbabilityMatrixController.setCurrentChain(currentChain);
                setProbabilityMatrixController.setMainDrawingPane(mainDrawingPane);
                setProbabilityMatrixController.setCurrentlySelectedStates(currentlySelectedStates);
                setProbabilityMatrixController.setCurrentlySelectedTransitions(currentlySelectedTransitions);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }

    }
}
