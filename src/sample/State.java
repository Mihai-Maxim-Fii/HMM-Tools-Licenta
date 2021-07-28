package sample;

import controllers.MainController;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class State {
    private Circle state;
    List<Transition> transitions;
    private Line testLine;
    private Label stateName;
    private Chain currentChain;
    private LineDistanceManipulator lineDistanceManipulator = new LineDistanceManipulator();

    public State clone() {
        State s = new State(this.state, this.currentChain);
        s.setTransitions(transitions);
        return s;
    }

    public State(Circle state, Chain currentChain) {
        transitions = new ArrayList<>();
        this.currentChain = currentChain;
        this.state = state;
        testLine = new Line(state.getCenterX(), state.getCenterY(), state.getCenterX(), state.getCenterY());
        testLine.setStrokeWidth(MainController.circleRadius * 1.8);
        testLine.setStroke(Color.WHITE);
        testLine.setStrokeLineCap(StrokeLineCap.ROUND);
        MainController.nrOfStates += 1;
        stateName = new Label();
        stateName.setText("S" + Integer.toString(MainController.nrOfStates));

        Font font = Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 12);
        stateName.setFont(font);
        stateName.setTextFill(Color.BLACK);
        updateStateNamePosition();

    }

    public void updateStateNamePosition() {


        Text theText = new Text(stateName.getText());
        theText.setFont(stateName.getFont());
        double width = theText.getBoundsInLocal().getWidth();
        /*
        while (width>MainController.circleRadius*2)
        {
            double size=stateName.getFont().getSize();
            stateName.setFont( Font.font("",FontWeight.BOLD,size-1));
            theText.setFont(stateName.getFont());
            width=theText.getBoundsInLocal().getWidth();
        }

         */
        Line ln = lineDistanceManipulator.getLineAlteredFromBeginning(state.getCenterX() - MainController.circleRadius / 2, state.getCenterY(), state.getCenterX() + MainController.circleRadius / 2, state.getCenterY(), (MainController.circleRadius - width) / 2);
        stateName.setLayoutX(ln.getStartX());
        if (width <= MainController.circleRadius * 2)
            stateName.setLayoutY(state.getCenterY() - 8);
        else
            putStateNameOutside();
    }

    public void putStateNameOutside() {
        for (Transition t : transitions) {
            if (t.getTo() == this) {

                if (MainController.getDistanceBetweenTwoPoints(t.getCubicCurve().getControlX1(), t.getCubicCurve().getControlY1(), stateName.getLayoutX(), stateName.getLayoutY() - MainController.circleRadius / 2 - 8) > MainController.getDistanceBetweenTwoPoints(t.getCubicCurve().getControlX1(), t.getCubicCurve().getControlY1(), stateName.getLayoutX(), stateName.getLayoutY() + MainController.circleRadius / 2)) {
                    stateName.setLayoutY(state.getCenterY() - 15 - MainController.circleRadius);
                } else {
                    stateName.setLayoutY(state.getCenterY() + MainController.circleRadius);
                }
                return;

            }
        }

        stateName.setLayoutY(state.getCenterY() - 15 - MainController.circleRadius);
        double minDist = Integer.MAX_VALUE;
        double minX = state.getCenterX(), minY = state.getCenterY();

        for (State s : currentChain.getStates()) {
            if (s != this)
                if (MainController.getDistanceBetweenTwoPoints(state.getCenterX(), state.getCenterY(), s.getState().getCenterX(), s.getState().getCenterY()) < minDist) {
                    minX = s.getState().getCenterX();
                    minY = s.getState().getCenterY();
                    minDist = MainController.getDistanceBetweenTwoPoints(state.getCenterX(), state.getCenterY(), s.getState().getCenterX(), s.getState().getCenterY());
                }
        }


        if (MainController.getDistanceBetweenTwoPoints(minX, minY, stateName.getLayoutX(), stateName.getLayoutY() - MainController.circleRadius / 2 - 8) > MainController.getDistanceBetweenTwoPoints(minX, minY, stateName.getLayoutX(), stateName.getLayoutY() + MainController.circleRadius / 2)) {
            stateName.setLayoutY(state.getCenterY() - 15 - MainController.circleRadius);
        } else {
            stateName.setLayoutY(state.getCenterY() + MainController.circleRadius);
        }


    }

    public Circle getState() {
        return state;
    }

    public void setState(Circle state) {
        this.state = state;
    }

    public List<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<Transition> transitions) {
        this.transitions = transitions;
    }

    public void setTestLine(Line testLine) {
        this.testLine = testLine;
    }

    public Label getStateName() {
        return stateName;
    }

    public void setStateName(Label stateName) {
        this.stateName = stateName;
    }


    public Line getTestLine() {
        return testLine;
    }
}

