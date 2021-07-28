package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sample.*;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.*;

public class ProbabilityMatrixController {

    @FXML
    private TextArea probabilityMatrix;
    @FXML
    private TextArea stateNames;
    @FXML
    private Button drawButton;
    @FXML
    private Button cancelButton;
    private List<State> currentlySelectedStates;
    private List<Transition> currentlySelectedTransitions;
    private Chain currentChain;
    private Pane mainDrawingPane;
    private double screenWidth, screenHeight;

    public ProbabilityMatrixController() {
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        screenHeight = screenBounds.getHeight();
        screenWidth = screenBounds.getWidth();


    }

    public double addTwoDoubles(double x, double y) {
        BigDecimal bigDecimalx = new BigDecimal(x);
        BigDecimal bigDecimaly = new BigDecimal(y);
        bigDecimalx = bigDecimalx.add(bigDecimaly);
        return bigDecimalx.doubleValue();
    }

    public double castToDouble(String x) {
        try {
            return Double.parseDouble(x);

        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void getIncidenceMatrixFromText() throws FileNotFoundException {
        String names = stateNames.getText();
        String probMatrix = probabilityMatrix.getText();
        int n1 = names.split(",").length;
        double probabilityMatrixDouble[][] = new double[n1][n1];

        if (probMatrix.split("\n").length != n1) {

            return;
        }
        int i = 0;
        int j = 0;
        for (String line : probMatrix.split("\n")) {
            j = 0;
            double sum = 0;
            line = line.trim().replaceAll("\\s+", " ");
            if (line.split(" ").length != n1) {

                return;
            }

            for (String str : line.split(" ")) {

                if (castToDouble(str) != -1) {
                    sum = addTwoDoubles(sum, castToDouble(str));
                    probabilityMatrixDouble[i][j] = castToDouble(str);

                } else {

                    return;
                }
                j += 1;
            }
            if (sum < 0.99 || sum > 1.0000000000000010) {

                return;
            }
            i += 1;
        }

        HashMap<String, Integer> stateOrder = new HashMap<>();
        TreeMap<Integer, String> stateOrderTree = new TreeMap<>();
        int stateCount = 0;
        for (String s : names.split(",")) {
            stateOrder.put(s, stateCount);
            stateOrderTree.put(stateCount, s);
            stateCount += 1;
        }

        IncidenceMatrixChain incidenceMatrixChain = new IncidenceMatrixChain(stateOrder, stateOrderTree, n1, probabilityMatrixDouble);
        makeChainFromIncidenceMatrix(incidenceMatrixChain);
        Stage st = (Stage) probabilityMatrix.getScene().getWindow();
        ProbabilityMatrixHandler.instanceNumber -= 1;
        st.close();

    }

    public void makeChainFromIncidenceMatrix(IncidenceMatrixChain incidenceMatrixChain) {
        double stateDistance = Math.min(screenHeight, screenWidth) * ((MainController.circleRadius / 2.0) / 100.0);

        List<State> states = new ArrayList<>();

        HashMap<Integer, State> stateMapper = new HashMap<>();
        double rn;
        double radius;
        if (incidenceMatrixChain.getStateCount() > 7)
            radius = 3;
        else
            radius = incidenceMatrixChain.getStateCount() / 2;

        for (int i = 0; i < incidenceMatrixChain.getStateCount(); i++) {
            Random rnd = new Random();
           /*
            double e1[] = getFirstElementFromArray(incidenceMatrixChain.getIncidenceMatrix()[i], incidenceMatrixChain.getStateCount() / 2, incidenceMatrixChain.getStateCount(), false);
            double e2[] = getFirstElementFromArray(incidenceMatrixChain.getIncidenceMatrix()[i], incidenceMatrixChain.getStateCount() / 2 - 1, 0, true);
            if (e1[0] < e2[0]) {
                rn = e1[1];
            } else if (e1[0] > e2[0]) {
                rn = e2[1];
            } else {
                if (Math.abs(rnd.nextInt() % 2) == 1) {
                    rn = e1[1];
                } else
                    rn = e2[1];
            }

            */

            rn = rnd.nextInt() % (radius + 1);
            if (incidenceMatrixChain.getStateCount() % 2 == 0) {
                if (rn < 0)
                    rn += 1;
            }

            rn = incidenceMatrixChain.getStateCount() / 2 + rn;


            String stateName = incidenceMatrixChain.getStateOrderTree().get(i);
            State f = makeState(i * stateDistance + 2 * MainController.circleRadius, rn * stateDistance + 2 * MainController.circleRadius, stateName);
            states.add(f);
            stateMapper.put(incidenceMatrixChain.getStateOrder().get(stateName), f);
        }

        double miny = Integer.MAX_VALUE;
        for (State s : states) {

            currentChain.getStates().add(s);
            if (s.getState().getCenterY() < miny)
                miny = s.getState().getCenterY();

        }
        double diff = miny - 150;

        for (State s : states) {
            s.getState().setCenterX(s.getState().getCenterX() + 50);
            s.getState().setCenterY(s.getState().getCenterY() - diff);
            s.getTestLine().setStartY(s.getTestLine().getStartY() - diff);
            s.getTestLine().setEndY(s.getTestLine().getEndY() - diff);
            s.getTestLine().setStartX(s.getTestLine().getStartX() + 50);
            s.getTestLine().setEndX(s.getTestLine().getEndX() + 50);
            s.getStateName().setLayoutY(s.getStateName().getLayoutY() - diff);
            s.getStateName().setLayoutX(s.getStateName().getLayoutX() + 50);

        }
        for (int i = 0; i < incidenceMatrixChain.getStateCount(); i++) {
            for (int j = 0; j < incidenceMatrixChain.getStateCount(); j++) {
                if (incidenceMatrixChain.getIncidenceMatrix()[i][j] > 0) {
                    if (i != j) {
                        addTransition(stateMapper.get(i), stateMapper.get(j), incidenceMatrixChain.getIncidenceMatrix()[i][j], false);
                    } else {
                        addTransition(stateMapper.get(i), stateMapper.get(j), incidenceMatrixChain.getIncidenceMatrix()[i][j], true);
                    }
                }
            }
        }
        for (State s : states) {
            mainDrawingPane.getChildren().addAll(s.getState(), s.getTestLine(), s.getStateName());
            s.updateStateNamePosition();
        }

    }

    public void addTransition(State s1, State s2, double probability, boolean selfTrans) {

        if (!selfTrans) {
            LineDistanceManipulator lineDistanceManipulator = new LineDistanceManipulator();
            Line transitionLine = lineDistanceManipulator.getLineAlteredFromBeginning(s1.getState().getCenterX(), s1.getState().getCenterY(), s2.getState().getCenterX(), s2.getState().getCenterY(), MainController.circleRadius);
            transitionLine = lineDistanceManipulator.getLineAlteredFromEnd(transitionLine.getStartX(), transitionLine.getStartY(), transitionLine.getEndX(), transitionLine.getEndY(), MainController.circleRadius);
            transitionLine.setStrokeWidth(MainController.lineWidth);

            Transition newTransition = new Transition(s1, s2, probability, transitionLine);
            newTransition.getCurve().setArrow();
            mainDrawingPane.getChildren().add(newTransition.getCubicCurve());
            mainDrawingPane.getChildren().add(newTransition.getCurve().getArrow());
            newTransition.getCurve().getProbabilityLabel().setText(Double.toString(probability));
            mainDrawingPane.getChildren().add(newTransition.getCurve().getProbabilityLabel());
            if (s1.getTransitions().contains(newTransition) == false) s1.getTransitions().add(newTransition);
        } else {
            drawCircularTransition(s1, probability);
        }

    }

    public Circle constructCircle(double x, double y, double radius) {
        Circle circle = new Circle(x, y, radius);
        circle.setFill(Color.WHITE);
        circle.setStrokeWidth(MainController.lineWidth);
        circle.setStroke(Color.BLACK);
        return circle;
    }

    public State makeState(double x, double y, String stateName) {
        StateNameHandler stateNameHandler = new StateNameHandler();
        State state = new State(constructCircle(x, y, MainController.circleRadius), currentChain);
        state.getStateName().setText(stateName);
        state.updateStateNamePosition();
        stateNameHandler.enableStateNameOnClick(state, currentChain.getStates());
        return state;


    }

    public static double getDistanceBetweenTwoPoints(double x1, double y1, double x2, double y2) {
        return Math.sqrt((Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));
    }

    public void drawCircularTransition(State s, double probability) {
        ProbabilityHandler probabilityHandler = new ProbabilityHandler();
        double x = s.getState().getCenterX();
        double y = s.getState().getCenterY();
        for (Transition t : s.getTransitions()) {
            if ((t.getFrom() == s) && (t.getTo() == s))
                return;
        }

        double minDist = Integer.MAX_VALUE;
        double minDistX, minDistY;
        minDistX = x;
        minDistY = y;
        double dst = -1;

        for (State f : currentChain.getStates()) {
            if (s != f) {
                dst = getDistanceBetweenTwoPoints(s.getState().getCenterX(), s.getState().getCenterY(), f.getState().getCenterX(), f.getState().getCenterY());
                if (dst < minDist) {
                    minDist = dst;
                    minDistX = f.getState().getCenterX();
                    minDistY = f.getState().getCenterY();
                }
            }

        }

        minDist = -1;

        Transition t = new Transition(s, s, -1, new Line(x, 0, 0, y));

        if ((x - MainController.selfTransitionSize > 0) && (y - MainController.selfTransitionSize > 0)) {

            double newDist = 0;
            newDist += getDistanceBetweenTwoPoints((x + x - MainController.selfTransitionSize) / 2, (y - MainController.selfTransitionSize + y) / 2, minDistX, minDistY);
            if (newDist > minDist) {
                t = new Transition(s, s, -1, new Line(x, y - MainController.circleRadius, x - MainController.circleRadius, y));
                t.getCubicCurve().setControlX1(x);
                t.getCubicCurve().setControlY1(y - MainController.selfTransitionSize);
                t.getCubicCurve().setControlX2(x - MainController.selfTransitionSize);
                t.getCubicCurve().setControlY2(y);
                minDist = newDist;
            }

        }
        if ((x + MainController.selfTransitionSize < mainDrawingPane.getWidth()) && (y - MainController.selfTransitionSize > 0)) {

            double newDist = 0;
            newDist += getDistanceBetweenTwoPoints((x + x + MainController.selfTransitionSize) / 2, (y - MainController.selfTransitionSize + y) / 2, minDistX, minDistY);
            if (newDist > minDist) {
                t = new Transition(s, s, -1, new Line(x, y - MainController.circleRadius, x + MainController.circleRadius, y));
                t.getCubicCurve().setControlX1(x);
                t.getCubicCurve().setControlY1(y - MainController.selfTransitionSize);
                t.getCubicCurve().setControlX2(x + MainController.selfTransitionSize);
                t.getCubicCurve().setControlY2(y);
                minDist = newDist;
            }

        }
        if ((x - MainController.selfTransitionSize > 0) && (y + MainController.selfTransitionSize > 0)) {
            double newDist = 0;
            newDist += getDistanceBetweenTwoPoints((x + x - MainController.selfTransitionSize) / 2, (y + MainController.selfTransitionSize + y) / 2, minDistX, minDistY);
            if (newDist > minDist) {
                t = new Transition(s, s, -1, new Line(x, y + MainController.circleRadius, x - MainController.circleRadius, y));
                t.getCubicCurve().setControlX1(x);
                t.getCubicCurve().setControlY1(y + MainController.selfTransitionSize);
                t.getCubicCurve().setControlX2(x - MainController.selfTransitionSize);
                t.getCubicCurve().setControlY2(y);
                minDist = newDist;
            }

        }
        if ((x + MainController.selfTransitionSize > 0) && (y + MainController.selfTransitionSize > 0)) {

            double newDist = 0;

            newDist += getDistanceBetweenTwoPoints((x + x + MainController.selfTransitionSize) / 2, (y + MainController.selfTransitionSize + y) / 2, minDistX, minDistY);

            if (newDist > minDist) {
                t = new Transition(s, s, -1, new Line(x, y + MainController.circleRadius, x + MainController.circleRadius, y));
                t.getCubicCurve().setControlX1(x);
                t.getCubicCurve().setControlY1(y + MainController.selfTransitionSize);
                t.getCubicCurve().setControlX2(x + MainController.selfTransitionSize);
                t.getCubicCurve().setControlY2(y);
                minDist = newDist;
            }

        }

        s.getTransitions().add(t);
        t.getCurve().setArrowSelf(t.getCubicCurve().getControlX2(), t.getCubicCurve().getControlY2());

        mainDrawingPane.getChildren().addAll(t.getCubicCurve(), t.getCurve().getArrow(), t.getCurve().getProbabilityLabel());
        t.getCurve().getCnt1().setCenterX(t.getCubicCurve().getControlX1());
        t.getCurve().getCnt1().setCenterY(t.getCubicCurve().getControlY1());

        t.getCurve().getCnt2().setCenterX(t.getCubicCurve().getControlX2());
        t.getCurve().getCnt2().setCenterY(t.getCubicCurve().getControlY2());

        CurveDragPoints curveDragPoints = new CurveDragPoints(mainDrawingPane);
        curveDragPoints.enableDragPoints(t.getCurve(), t, currentChain.getStates(), currentlySelectedTransitions, currentlySelectedStates, null);
        probabilityHandler.enableOnClickProbability(t);
        t.getCurve().selectionMode(false);


        t.setProbability(probability);
        t.getCurve().getProbabilityLabel().setText(Double.toString(probability));


    }


    public Chain getCurrentChain() {
        return currentChain;
    }

    public void setCurrentChain(Chain currentChain) {
        this.currentChain = currentChain;
    }

    public Pane getMainDrawingPane() {
        return mainDrawingPane;
    }

    public void setMainDrawingPane(Pane mainDrawingPane) {
        this.mainDrawingPane = mainDrawingPane;
    }

    public void setCurrentlySelectedStates(List<State> currentlySelectedStates) {
        this.currentlySelectedStates = currentlySelectedStates;
    }

    public void setCurrentlySelectedTransitions(List<Transition> currentlySelectedTransitions) {
        this.currentlySelectedTransitions = currentlySelectedTransitions;
    }

    public void drawChain(ActionEvent actionEvent) throws FileNotFoundException {
        getIncidenceMatrixFromText();


    }

    public void closeWindow(ActionEvent actionEvent) {
        ProbabilityMatrixHandler.instanceNumber -= 1;
        Stage st = (Stage) cancelButton.getScene().getWindow();
        st.close();
    }
}
