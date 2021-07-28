package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.*;

import java.math.BigDecimal;
import java.util.List;

public class SetStepProbabilityController {


    @FXML
    private Button setProbabilityButton;
    @FXML
    private TextField probabilityChoice;
    @FXML
    private VBox mainVbox;

    private List<StepProbability> stepProbabilities;
    private StepProbability stepProbability;
    private List<MenuItem> items;
    @FXML
    private BorderPane borderPane;
    private boolean hasObservations = false;
    private Label infoLabel;
    private String error1 = "Probability must be a \n number between \n [0.001,1]";
    private String error2 = "Your probabilities add \n up to: ";


    public BigDecimal getProbabilityTotal() {


        BigDecimal sum = BigDecimal.ZERO;
        for (StepProbability s : stepProbabilities) {
            if ((s.getStepProbability() != -1) && (stepProbability.getLabel() != s.getLabel()))
                sum = sum.add(BigDecimal.valueOf(s.getStepProbability()));
        }

        return sum;


    }

    public void init(List<StepProbability> stepProbabilities, StepProbability stepProbability, List<MenuItem> items, boolean hasObservations) {
        this.stepProbability = stepProbability;
        this.stepProbabilities = stepProbabilities;
        this.items = items;
        this.hasObservations = hasObservations;
    }

    public void setProbability(MouseEvent mouseEvent) {

        //Double doub=-1.0;
        BigDecimal doub = BigDecimal.valueOf(-1);
        boolean change = true;
        boolean overOne = false;
        try {

            doub = BigDecimal.valueOf(Double.parseDouble(probabilityChoice.getText()));

        } catch (NumberFormatException ex) {
            change = false;

        }
        if (((change) && ((doub.compareTo(BigDecimal.valueOf(1.0001)) == -1) && (doub.compareTo(BigDecimal.ZERO) == 1)) && (probabilityChoice.getText().length() <= 5) && (getProbabilityTotal().add(doub).compareTo(BigDecimal.valueOf(1.0001)) == -1))||(doub.compareTo(BigDecimal.ZERO) == 0)) {

            stepProbability.getLabel().setText(doub.toString());
            stepProbability.setStepProbability(Double.valueOf(doub.toString()));
            Stage st = (Stage) setProbabilityButton.getScene().getWindow();
            updateStateNamePosition(stepProbability.getLabel(), stepProbability.getState());
            // stepProbability.getLabel().setTextFill(Paint.valueOf("0040ff"));

            if ((getProbabilityTotal().add(doub).compareTo(BigDecimal.valueOf(0.99)) == 1) && (getProbabilityTotal().add(doub).compareTo(BigDecimal.valueOf(1.0000000000000010)) == -1)) {
                boolean cont = true;
                for (StepProbability stepProbability1 : stepProbabilities) {
                    if (stepProbability1.getStepProbability() < 0) {
                        cont = false;
                        break;
                    }
                }
                if (cont == true) {
                    for (StepProbability stepProbability1 : stepProbabilities) {
                        stepProbability1.getLabel().setTextFill(Paint.valueOf("0040ff"));
                    }

                }
                if (hasObservations)
                    for (MenuItem item : items) {
                        item.setDisable(false);
                    }
            } else {
                for (StepProbability stepProbability1 : stepProbabilities) {
                    stepProbability1.getLabel().setTextFill(Paint.valueOf("8b0000"));
                }

                for (MenuItem item : items) {
                    item.setDisable(true);
                }

            }


            SimulatorController.windowCount -= 1;
            st.close();

        } else {
            if ((getProbabilityTotal().add(doub).compareTo(BigDecimal.valueOf(1.0000000000000010)) == 1))
                overOne = true;

            if (infoLabel == null) {
                infoLabel = new Label();
            }
            Stage st = (Stage) setProbabilityButton.getScene().getWindow();
            if (overOne) {
                if (getProbabilityTotal().add(doub).toString().length() > 4)
                    infoLabel.setText(error2 + getProbabilityTotal().add(doub).toString().substring(0, 4));
                else
                    infoLabel.setText(error2 + getProbabilityTotal().add(doub).toString());
                st.setHeight(120 + 24);
            } else {
                infoLabel.setText(error1);
                st.setHeight(120 + 50);
            }
            borderPane.setTop(infoLabel);


        }


    }


    public void cancelSetProbability(MouseEvent mouseEvent) {
        SimulatorController.windowCount -= 1;
        Stage st = (Stage) setProbabilityButton.getScene().getWindow();
        st.close();

    }

    public void updateStateNamePosition(Label label, State state) {
        Text theText = new Text(label.getText());
        theText.setFont(label.getFont());
        double width = theText.getBoundsInLocal().getWidth();

        LineDistanceManipulator lineDistanceManipulator = new LineDistanceManipulator();
        Line ln = lineDistanceManipulator.getLineAlteredFromBeginning(state.getState().getCenterX() - MainController.circleRadius / 2, state.getState().getCenterY(), state.getState().getCenterX() + MainController.circleRadius / 2, state.getState().getCenterY(), (MainController.circleRadius - width) / 2);

        label.setLayoutX(ln.getStartX());
        label.setLayoutY(state.getState().getCenterY() - (MainController.circleRadius / 1.4));
    }


}
