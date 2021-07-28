package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import sample.Observation;
import sample.ObservationNameHandler;

public class SetObservationNameController {
    private Observation observation;
    @FXML
    private TextField observationNameChoice;


    public void setObservationName(MouseEvent mouseEvent) {


        if ((observationNameChoice.getText().length() < 25) && (observationNameChoice.getText().length() > 0)) {
            Font font = Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 12);
            observation.getObservationName().setFont(font);
            observation.getObservationName().setText(observationNameChoice.getText());
            observation.updateObservationNamePosition();
            Stage st = (Stage) observationNameChoice.getScene().getWindow();
            ObservationNameHandler.instanceNumber -= 1;
            st.close();
        }

    }

    public void cancelSetStateNameProbability(MouseEvent mouseEvent) {

        Stage st = (Stage) observationNameChoice.getScene().getWindow();
        ObservationNameHandler.instanceNumber -= 1;
        st.close();

    }

    public Observation getObservation() {
        return observation;
    }

    public void setObservation(Observation observation) {
        this.observation = observation;
    }

    public void initObservation(Observation observation) {
        this.observation = observation;

    }
}
