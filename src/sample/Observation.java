package sample;

import controllers.MainController;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class Observation {
    private Rectangle observation;
    private List<State> states;
    private Chain curretChain;


    private Label observationName;
    private LineDistanceManipulator lineDistanceManipulator = new LineDistanceManipulator();
    private Point origin;

    public Observation(double x, double y, Chain currentChain) {
        states = new ArrayList<>();
        this.curretChain = currentChain;
        origin = new Point(x, y);
        observation = new Rectangle();
        observation.setX(x - MainController.observationSide / 2);
        observation.setY(y - MainController.observationSide / 2);
        observation.setWidth(MainController.observationSide);
        observation.setHeight(MainController.observationSide);
        observation.setStrokeWidth(MainController.lineWidth);
        observation.setFill(Color.WHITE);
        observation.setStroke(Color.BLACK);

        observationName = new Label();
        observationName.setText("O" + Integer.toString(MainController.nrOfObservations));

        Font font = Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 12);
        observationName.setFont(font);
        observationName.setTextFill(Color.BLACK);
        updateObservationNamePosition();

    }

    public void updateObservationNamePosition() {
        Text theText = new Text(observationName.getText());
        theText.setFont(observationName.getFont());
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
        Line ln = lineDistanceManipulator.getLineAlteredFromBeginning(origin.getX() - MainController.observationSide / 2, origin.getY(), origin.getX() + MainController.observationSide / 2, origin.getY(), (MainController.observationSide - width) / 2);
        observationName.setLayoutX(ln.getStartX());
        if (width <= MainController.observationSide)
            observationName.setLayoutY(origin.getY() - 8);
        else
            putStateNameOutside();
    }

    public void putStateNameOutside() {
        double x = origin.getX(), y = origin.getY(), dist = Integer.MAX_VALUE;

        for (State s : curretChain.getStates()) {
            if (lineDistanceManipulator.getDistance(s.getState().getCenterX(), s.getState().getCenterY(), origin.getX(), origin.getY()) < dist) {
                dist = lineDistanceManipulator.getDistance(s.getState().getCenterX(), s.getState().getCenterY(), origin.getX(), origin.getY());
                x = s.getState().getCenterX();
                y = s.getState().getCenterY();

            }
        }
       /*
       for(Observation obs:curretChain.getObservations())
       {
           if(this!=obs) {
               if(lineDistanceManipulator.getDistance(obs.getOrigin().getX(), obs.getOrigin().getY(), origin.getX(), origin.getY())<dist) {
                   dist = lineDistanceManipulator.getDistance(obs.getOrigin().getX(), obs.getOrigin().getY(), origin.getX(), origin.getY());
                   x = obs.getOrigin().getX();
                   y = obs.getOrigin().getY();
               }
           }
       }

        */

        if (lineDistanceManipulator.getDistance(origin.getX(), origin.getY() + MainController.observationSide / 2, x, y) > lineDistanceManipulator.getDistance(origin.getX(), origin.getY() - MainController.observationSide / 2, x, y)) {
            observationName.setLayoutY(origin.getY() + MainController.observationSide / 2);
        } else
            observationName.setLayoutY(origin.getY() - MainController.observationSide);


    }

    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }


    public Rectangle getObservation() {
        return observation;
    }

    public void setObservation(Rectangle observation) {
        this.observation = observation;
    }

    public Label getObservationName() {
        return observationName;
    }

    public void setObservationName(Label observationName) {
        this.observationName = observationName;
    }


    public LineDistanceManipulator getLineDistanceManipulator() {
        return lineDistanceManipulator;
    }

    public void setLineDistanceManipulator(LineDistanceManipulator lineDistanceManipulator) {
        this.lineDistanceManipulator = lineDistanceManipulator;
    }

    public Point getOrigin() {
        return origin;
    }

    public void setOrigin(Point origin) {
        this.origin = origin;
    }

    public Chain getCurretChain() {
        return curretChain;
    }

    public void setCurretChain(Chain curretChain) {
        this.curretChain = curretChain;
    }
}
