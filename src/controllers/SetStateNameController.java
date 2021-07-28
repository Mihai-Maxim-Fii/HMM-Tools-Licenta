package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import sample.State;
import sample.StateNameHandler;

import java.util.List;

public class SetStateNameController {

    @FXML
    private Button setStateNameButton;
    @FXML
    private TextField stateNameChoice;
    @FXML
    private VBox mainVbox;
    @FXML
    private BorderPane borderPane;
    private Label infoLabel;
    private String error1 = "Max name length is 25";

    private State state;


    private List<State> currentStates;

    public SetStateNameController() {

    }


    public void setStateName(MouseEvent mouseEvent) {

        boolean e1 = false, e2 = false;
        if (true) {

            if (stateNameChoice.getText().length() < 25) {
                Font font = Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 12);
                state.getStateName().setFont(font);
                state.getStateName().setText(stateNameChoice.getText());
                state.updateStateNamePosition();
            } else {
                e1 = true;

            }

        }

        Stage st = (Stage) setStateNameButton.getScene().getWindow();
        if ((e1 == true)) {
            if (infoLabel == null) {
                infoLabel = new Label();
            }
            st.setHeight(120 + 50);
            infoLabel.setText(error1);
            borderPane.setTop(infoLabel);

        } else {
            st.close();
            StateNameHandler.instanceNumber -= 1;
        }


    }

    public void cancelSetStateNameProbability(MouseEvent mouseEvent) {
        Stage st = (Stage) setStateNameButton.getScene().getWindow();
        st.close();
        StateNameHandler.instanceNumber -= 1;
    }

    public void setState(State state) {
        this.state = state;
    }


    public void setCurrentStates(List<State> currentStates) {
        this.currentStates = currentStates;
    }
}
