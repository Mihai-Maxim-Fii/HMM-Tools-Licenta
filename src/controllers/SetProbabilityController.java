package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sample.ProbabilityHandler;
import sample.Transition;

import java.math.BigDecimal;

public class SetProbabilityController {
    @FXML
    private Button setProbabilityButton;
    @FXML
    private TextField probabilityChoice;
    @FXML
    private VBox mainVbox;
    @FXML
    private BorderPane borderPane;
    private Label infoLabel;
    private String error1 = "Probability must be a \n number between \n [0.001,1]";
    private String error2 = "Your probabilities add \n up to: ";


    private Transition transition;

    public BigDecimal getProbabilityTotal() {

        BigDecimal sum = BigDecimal.ZERO;
        //double sum=0;
        if (transition.getObsTo() != null) return BigDecimal.ZERO;
        else
            for (Transition t : transition.getFrom().getTransitions()) {

                if (t.getObsTo() == null) {
                    if (t.getProbability() != -1 && t != transition)
                        sum = sum.add(BigDecimal.valueOf(t.getProbability()));
                }

            }
        return sum;
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
        if ((change) && ((doub.compareTo(BigDecimal.valueOf(1.0001)) == -1) && (doub.compareTo(BigDecimal.ZERO) == 1)) && (probabilityChoice.getText().length() <= 5) && (getProbabilityTotal().add(doub).compareTo(BigDecimal.valueOf(1.0001)) == -1)) {
            transition.getCurve().getProbabilityLabel().setText(doub.toString());
            transition.setProbability(Double.valueOf(doub.toString()));
            ProbabilityHandler.instanceNumber -= 1;

            Stage st = (Stage) setProbabilityButton.getScene().getWindow();
            st.close();

        } else {
            if ((getProbabilityTotal().add(doub).compareTo(BigDecimal.ONE) == 1)) {
                overOne = true;

            }
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

    public void setTransition(Transition transition) {
        this.transition = transition;
    }

    public void cancelSetProbability(MouseEvent mouseEvent) {
        ProbabilityHandler.instanceNumber -= 1;
        Stage st = (Stage) setProbabilityButton.getScene().getWindow();
        st.close();

    }
}
