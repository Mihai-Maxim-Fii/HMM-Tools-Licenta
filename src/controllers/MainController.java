package controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sample.*;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;


public class MainController {

    public static double selfTransitionSize = 100; //100,134,167
    public static int circleRadius = 30; //30 40 50
    public static double lineWidth = 2;  //  2 ,2.5,3
    public static double observationSide = 40; //40 50 60
    public static double arrowWidth = 5;  // 5 ,6.666,8.333
    public static double arrowLength = 10; // 10 ,13.333,16.666
    public Pane fatherPane;
    public BorderPane mainBorderPane;
    public CheckMenuItem v50;
    public CheckMenuItem v75;
    public CheckMenuItem v100;
    public CheckMenuItem soSm;
    public CheckMenuItem soMe;
    public CheckMenuItem soBi;
    public CheckMenuItem lSm;
    public CheckMenuItem lMe;
    public CheckMenuItem lBi;

    private double screenWidth, screenHeight;
    private IncidenceMatrixChain incidenceMatrixChain = null;

    @FXML
    private ScrollPane mainScrollPane;
    @FXML
    private ImageView stateView;
    @FXML
    private ImageView observationView;
    @FXML
    private ImageView transitionView;
    @FXML
    private ImageView selectionCursorView;
    @FXML
    private ImageView multipleSelectView;

    @FXML
    private Pane mainDrawingPane;
    @FXML
    private VBox topData;

    private double viewScale = -1;
    private double lastViewScale = -1;

    private boolean validationMode = false;
    boolean didOnce = false;
    boolean resized = false;
    public static int nrOfStates = 0;
    public static int nrOfObservations = 0;

    private List<State> multipleSelectStates = null;


    private Stage probabilityStage = new Stage();

    public MainController() {
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        screenHeight = screenBounds.getHeight();
        screenWidth = screenBounds.getWidth();


    }


    public Option currentOption = Option.NONE;

    private Chain currentChain = new Chain();

    private Selection selectionMode = Selection.FALSE;

    private List<State> currentlySelectedStates = new ArrayList<>();

    private Observation currentlySelectedObservation;

    private List<Transition> currentlySelectedTransitions = new ArrayList<>();


    private MakeDraggableState makeDraggableState;

    private DragLine lineDragger;


    private void restoreTestLinesAndDragListenersForCurrentlySelected() {

        DragLine dragLine = new DragLine(mainDrawingPane.getWidth(), mainDrawingPane.getHeight(), circleRadius, mainDrawingPane, currentChain);
        for (State s : currentChain.getStates()) {

            if (!currentlySelectedStates.contains(s)) {
                s.getTestLine().setStrokeWidth(1.8 * circleRadius);
                mainDrawingPane.getChildren().remove(s.getTestLine());
                mainDrawingPane.getChildren().remove(s.getStateName());
                mainDrawingPane.getChildren().addAll(s.getTestLine(), s.getStateName());
            } else {
                s.getTestLine().setStrokeWidth(1.8 * circleRadius);
            }


        }
    }


    public void saveProject() throws IOException, ClassNotFoundException {
        SerializableChain serializableChain = new SerializableChain();
        for (State s : currentChain.getStates()) {
            SerializableState serializableState = new SerializableState(s.getStateName().getText(), new Point(s.getState().getCenterX(), s.getState().getCenterY()));
            serializableChain.getStates().add(serializableState);
            for (Transition t : s.getTransitions()) {
                String from = t.getFrom().getStateName().getText() + t.getFrom().getState().getCenterX() + t.getFrom().getState().getCenterY();
                String to;
                if (t.getObsTo() == null)
                    to = t.getTo().getStateName().getText() + t.getTo().getState().getCenterX() + t.getTo().getState().getCenterY();
                else
                    to = t.getObsTo().getObservationName().getText() + t.getObsTo().getOrigin().getX() + t.getObsTo().getOrigin().getY();
                List<Point> curvePoints = new ArrayList<>();
                curvePoints.add(new Point(t.getCurve().getCubicCurve().getStartX(), t.getCubicCurve().getStartY()));
                curvePoints.add(new Point(t.getCurve().getCubicCurve().getControlX1(), t.getCubicCurve().getControlY1()));
                curvePoints.add(new Point(t.getCurve().getCubicCurve().getControlX2(), t.getCubicCurve().getControlY2()));
                curvePoints.add(new Point(t.getCurve().getCubicCurve().getEndX(), t.getCubicCurve().getEndY()));
                List<Point> dragPoints = new ArrayList<>();
                dragPoints.add(new Point(t.getCurve().getCnt1().getCenterX(), t.getCurve().getCnt1().getCenterY()));
                dragPoints.add(new Point(t.getCurve().getCnt2().getCenterX(), t.getCurve().getCnt2().getCenterY()));


                SerializableTransition serializableTransition = new SerializableTransition(from, to, curvePoints, dragPoints, t.getProbability(), t.getCurve().isLineMode());
                serializableChain.getTransitions().add(serializableTransition);
            }
        }
        for (Observation o : currentChain.getObservations()) {
            SerializableObservation serializableObservation = new SerializableObservation(o.getObservationName().getText(), new Point(o.getOrigin().getX(), o.getOrigin().getY()));
            serializableChain.getObservations().add(serializableObservation);
        }

        serializableChain.setScreenWidth(mainDrawingPane.getWidth());
        serializableChain.setScreenHeight(mainDrawingPane.getHeight());
        resetBoard();

        drawProjectFromSerializableClass(serializableChain);
        MakeDraggableState makeDraggableState = new MakeDraggableState(mainDrawingPane.getWidth(), mainDrawingPane.getHeight(), circleRadius);

        for (State s : currentChain.getStates()) {

            makeDraggableState.renewTransitions(s, currentChain, mainDrawingPane, 0.1, 0.1);
            for (Transition t : s.getTransitions()) {
                if (t.getObsTo() != null) {
                    makeDraggableState.renewTransitionsObservation(s, t.getObsTo(), currentChain, mainDrawingPane, 0.1, 0.1);
                }
            }
        }


    }

    private void redraw() throws IOException, ClassNotFoundException {
        double lastView = viewScale;
        saveProject();
        if (lastView == 0.5)
            view50();
        if (lastView == 0.75)
            view75();
        if (lastView == 1)
            view100();

    }


    private void restoreTestLine(Line line, double originX, double originY) {
        line.setStartX(originX);
        line.setStartY(originY);
        line.setEndX(originX);
        line.setEndY(originY);
        line.setStrokeWidth(MainController.circleRadius * 1.8);
        line.setStroke(Color.WHITE);
        line.setStrokeLineCap(StrokeLineCap.ROUND);

    }


    public void chooseState(MouseEvent mouseEvent) {

        if (currentOption == Option.STATE) {
            setCurrentOption(Option.NONE);
            resetHighlight();
            disableProbabilityListeners();
            disableStateNameOnCick();
        } else {
            disableValidationMode();
            disableSelectionMode();
            resetHighlight();
            setCurrentOption(Option.STATE);
            highlightOption();
            enableStateNameOnClick();
        }
    }

    public void chooseObservation(MouseEvent mouseEvent) {

        if (currentOption == Option.OBSERVATION) {
            setCurrentOption(Option.NONE);
            resetHighlight();
            disableProbabilityListeners();
            disableObservationNameHandlers();
        } else {
            disableValidationMode();
            disableSelectionMode();
            resetHighlight();
            setCurrentOption(Option.OBSERVATION);
            highlightOption();
            enableObservationNameHandlers();
            disableStateNameOnCick();
        }
    }

    public void chooseTransition(MouseEvent mouseEvent) {


        boolean straighten = false;
        CubicCurveCreator cubicCurveCreator = new CubicCurveCreator();
        for (State s : currentChain.getStates()) {
            for (Transition t : s.getTransitions()) {
                if ((t.getCurve().isSelectedMode()) && (t.getFrom() != t.getTo())) {
                    cubicCurveCreator.straightenCurve(t);
                    straighten = true;
                }
                if ((t.getFrom() == t.getTo()))
                    straighten = false;
            }
        }
        if (!straighten) {

            if (currentOption == Option.TRANSITION) {
                disableSelectionMode();
                setCurrentOption(Option.NONE);
                resetHighlight();
                disableProbabilityListeners();
                disableStateNameOnCick();
            } else {


                if (currentlySelectedObservation != null) {
                    deselectObservation(currentlySelectedObservation);
                }

                disableValidationMode();
                resetHighlight();
                setCurrentOption(Option.TRANSITION);
                highlightOption();
                enableProbabilityListeners();
                disableStateNameOnCick();
                lineDragger = new DragLine(mainDrawingPane.getWidth(), mainDrawingPane.getHeight(), circleRadius, mainDrawingPane, currentChain);
                for (State s : currentChain.getStates()) {
                    lineDragger.enableDrag(s.getTestLine());
                }
            }

        }
    }

    public void colorSelectedState(State s, Color c) {
        s.getState().setStroke(c);
        s.getStateName().setTextFill(c);

    }

    public double[] getFirstElementFromArray(double[] array, int i, int n, boolean reversed) {
        double d[];
        if (!reversed) {
            int cnt = 0;
            for (int j = i; j < n; j++) {
                if (array[j] != 0) {
                    d = new double[]{cnt, j};
                    return d;
                }
                cnt += 1;
            }


        } else {
            int cnt = 0;
            for (int j = i; j >= 0; j--) {
                if (array[j] != 0) {
                    d = new double[]{cnt, j};
                    return d;
                }
                cnt += 1;
            }
        }
        d = new double[]{100, 100};
        return d;

    }

    public void makeChainFromIncidenceMatrix(IncidenceMatrixChain incidenceMatrixChain) {
        double stateDistance = Math.min(screenHeight, screenWidth) * 0.1;
        List<State> states = new ArrayList<>();

        HashMap<Integer, State> stateMapper = new HashMap<>();
        double rn;
        for (int i = 0; i < incidenceMatrixChain.getStateCount(); i++) {
            Random rnd = new Random();

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


            String stateName = incidenceMatrixChain.getStateOrderTree().get(i);
            State f = makeState(i * stateDistance + circleRadius + 15, rn * stateDistance + circleRadius + 15, stateName);
            states.add(f);
            stateMapper.put(incidenceMatrixChain.getStateOrder().get(stateName), f);
        }

        for (State s : states) {
            mainDrawingPane.getChildren().addAll(s.getState(), s.getTestLine(), s.getStateName());
            currentChain.getStates().add(s);

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


    }


    public State makeState(double x, double y, String stateName) {
        StateNameHandler stateNameHandler = new StateNameHandler();
        State state = new State(constructCircle(x, y, circleRadius), currentChain);
        state.getStateName().setText(stateName);
        state.updateStateNamePosition();
        stateNameHandler.enableStateNameOnClick(state, currentChain.getStates());
        return state;


    }

    public void addTransition(State s1, State s2, double probability, boolean selfTrans) {

        if (!selfTrans) {
            LineDistanceManipulator lineDistanceManipulator = new LineDistanceManipulator();
            Line transitionLine = lineDistanceManipulator.getLineAlteredFromBeginning(s1.getState().getCenterX(), s1.getState().getCenterY(), s2.getState().getCenterX(), s2.getState().getCenterY(), MainController.circleRadius);
            transitionLine = lineDistanceManipulator.getLineAlteredFromEnd(transitionLine.getStartX(), transitionLine.getStartY(), transitionLine.getEndX(), transitionLine.getEndY(), MainController.circleRadius);
            transitionLine.setStrokeWidth(lineWidth);

            Transition newTransition = new Transition(s1, s2, probability, transitionLine);
            newTransition.getCurve().setArrow();
            mainDrawingPane.getChildren().add(newTransition.getCubicCurve());
            mainDrawingPane.getChildren().add(newTransition.getCurve().getArrow());
            newTransition.getCurve().getProbabilityLabel().setText(Double.toString(probability));
            mainDrawingPane.getChildren().add(newTransition.getCurve().getProbabilityLabel());
            if (!s1.getTransitions().contains(newTransition)) s1.getTransitions().add(newTransition);
        } else {
            drawCircularTransition(s1, probability);
        }

    }


    public void removeDragClickListeners(State s) {
        s.getState().setOnMouseClicked(null);
        s.getState().setOnMouseReleased(null);
        s.getState().setOnMouseDragged(null);
        s.getState().setOnMouseEntered(null);
        s.getState().setOnMouseExited(null);


    }

    public void removeDragClickListenersFromTransition(Transition t) {

        t.getCubicCurve().setOnMouseReleased(null);
        t.getCubicCurve().setOnMouseDragged(null);
        t.getCubicCurve().setOnMouseEntered(null);
        t.getCubicCurve().setOnMouseExited(null);
        t.getCubicCurve().setOnMouseClicked(null);


    }

    public void removeDragFromAllLines() {

        for (State s : currentChain.getStates()) {
            removeDragClickListenersFromLine(s.getTestLine());
        }


    }

    public static void removeDragClickListenersFromLine(Line s) {
        s.setOnMouseClicked(null);
        s.setOnMouseReleased(null);
        s.setOnMouseDragged(null);
        s.setOnMouseEntered(null);
        s.setOnMouseExited(null);

    }

    public void chooseSelection(MouseEvent mouseEvent) {

        if (currentOption == Option.SELECT) {
            resetHighlight();
            disableSelectionMode();
            setCurrentOption(Option.NONE);
        } else {
            disableValidationMode();
            resetHighlight();
            setCurrentOption(Option.SELECT);
            disableProbabilityListeners();
            highlightOption();
            disableStateNameOnCick();
        }


        if ((selectionMode == Selection.FALSE) && (currentOption != Option.NONE)) {


            enableSelectionMode(mouseEvent);


        } else {

            disableSelectionMode();

        }

    }


    public void enableProbabilityListeners() {
        ProbabilityHandler probabilityHandler = new ProbabilityHandler();
        for (State s : currentChain.getStates()) {
            for (Transition t : s.getTransitions()) {
                probabilityHandler.enableOnClickProbability(t);
            }
        }
    }

    public void disableProbabilityListeners() {
        ProbabilityHandler probabilityHandler = new ProbabilityHandler();
        for (State s : currentChain.getStates()) {
            for (Transition t : s.getTransitions()) {
                probabilityHandler.disableOnClickProbability(t);
            }
        }
    }


    public void enableSelectionMode(MouseEvent mouseEvent) {

        selectionMode = Selection.TRUE;
        drawShape(mouseEvent);
        removeDragFromAllLines();

        for (State s : currentChain.getStates()) {
            s.getTestLine().setStrokeWidth(0);
            for (Transition t : s.getTransitions()) {
                if (t.getCurve().getCubicCurve() != null) {
                    CurveDragPoints curveDragPoints = new CurveDragPoints(mainDrawingPane);

                    curveDragPoints.enableDragPoints(t.getCurve(), t, currentChain.getStates(), currentlySelectedTransitions, currentlySelectedStates, currentChain);

                }
            }

        }
    }

    public void disableObservationNameHandlers() {
        ObservationNameHandler observationNameHandler = new ObservationNameHandler();

        for (Observation o : currentChain.getObservations()) {
            observationNameHandler.disableObservationNameOnClick(o);

        }
    }

    public void enableObservationNameHandlers() {
        ObservationNameHandler observationNameHandler = new ObservationNameHandler();

        for (Observation o : currentChain.getObservations()) {
            observationNameHandler.enableObservationNameOnClick(o);

        }

    }

    public void disableSelectionMode() {
        selectionMode = Selection.FALSE;
        currentOption = Option.NONE;
        resetCurrentlySelectedStates();
        removeDragFromAllLines();
        currentlySelectedObservation = null;


        for (State s : currentChain.getStates()) {
            for (Transition t : s.getTransitions()) {
                if (mainDrawingPane.getChildren().contains(t.getCurve().getCnt1())) {
                    mainDrawingPane.getChildren().removeAll(t.getCurve().getCnt1(), t.getCurve().getCnt2(), t.getCurve().lb1, t.getCurve().lb2);
                }
                t.getCurve().selectionMode(false);
                removeDragClickListenersFromTransition(t);
                colorState(s, Color.BLACK);

            }

            // StateNameHandler stateNameHandler = new StateNameHandler();
            // stateNameHandler.enableStateNameOnClick(s, currentChain.getStates());
        }

        for (Observation o : currentChain.getObservations()) {
            deselectObservation(o);
        }
        disableObservationNameHandlers();

    }

    public void resetCurrentlySelectedStates() {
        for (State s : currentlySelectedStates) {
            colorSelectedState(s, Color.BLACK);
            s.getState().setOnMouseClicked(null);
            removeDragClickListeners(s);


        }
        restoreTestLinesAndDragListenersForCurrentlySelected();
        currentlySelectedStates = new ArrayList<>();
    }

    public void highlightOption() {
        resetHighlight();
        if (currentOption == Option.STATE)
            stateView.setImage(new Image("images/redCircle.png"));
        else if (currentOption == Option.OBSERVATION)
            observationView.setImage(new Image("images/redSquare.png"));
        else if (currentOption == Option.TRANSITION)
            transitionView.setImage(new Image("images/redArrow.png"));
        else if (currentOption == Option.SELECT) {
            selectionCursorView.setImage(new Image("images/redCursor.png"));
        } else if (currentOption == Option.MULTIPLESELECT) {
            multipleSelectView.setImage(new Image("images/redMultipleSelect.png"));
        }
    }

    public void resetHighlight() {
        stateView.setImage(new Image("images/circle.png"));
        transitionView.setImage(new Image("images/arrow.png"));
        observationView.setImage(new Image("images/square.png"));
        selectionCursorView.setImage(new Image("images/cursor.png"));
        multipleSelectView.setImage(new Image("images/multipleSelect.png"));
    }

    public void drawShape(MouseEvent mouseEvent) {
        if (!validationMode) {
            if (selectionMode == Selection.FALSE) {
                switch (getCurrentOption()) {
                    case STATE:
                        drawState(mouseEvent);
                        break;
                    case TRANSITION:
                        drawTransition(mouseEvent);
                        break;
                    case OBSERVATION:
                        drawObservation(mouseEvent);
                }
            } else {


                selectStates(mouseEvent);

            }

        } else {
            validateChain(mouseEvent);

        }
    }

    public List<Double> getInBoundsObs(double x, double y, double side) {
        side = side + 2;

        double newx = x, newy = y;
        List<Double> newCoords = new ArrayList<>();
        if (y + side / 2 > mainDrawingPane.getHeight()) {
            newy = mainDrawingPane.getHeight() - side / 2;
        }
        if (y - side / 2 < 0) {
            newy = side / 2;
        }
        if (x + side / 2 > mainDrawingPane.getWidth()) {
            newx = mainDrawingPane.getWidth() - side / 2;
        }
        if (x - side / 2 < 0) {
            newx = side / 2;
        }
        newCoords.add(newx);
        newCoords.add(newy);
        return newCoords;

    }

    private void drawObservation(MouseEvent mouseEvent) {
        List<Double> coords = getInBounds(mouseEvent.getX(), mouseEvent.getY(), observationSide / 2 + 3);
        if (checkStateOverlap(coords.get(0), coords.get(1), circleRadius)) {

            nrOfObservations += 1;
            Observation observation = new Observation(coords.get(0), coords.get(1), currentChain);
            currentChain.getObservations().add(observation);
            mainDrawingPane.getChildren().add(observation.getObservation());
            mainDrawingPane.getChildren().add(observation.getObservationName());
            enableObservationOnClick(observation);
            ObservationNameHandler observationNameHandler = new ObservationNameHandler();
            observationNameHandler.enableObservationNameOnClick(observation);

        }

    }

    public void enableObservationDrag(Observation observation) {
        MakeDraggableState makeDraggableState = new MakeDraggableState(mainDrawingPane.getWidth(), mainDrawingPane.getHeight(), circleRadius);
        observation.getObservation().setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                if ((mouseEvent.getY() > mainDrawingPane.getHeight() - observationSide / 2)) {
                    mainDrawingPane.setPrefHeight(mainDrawingPane.getHeight() + 200);
                }

                if ((mouseEvent.getX() > mainDrawingPane.getWidth() - observationSide / 2)) {
                    mainDrawingPane.setPrefWidth(mainDrawingPane.getWidth() + 200);
                }

                double newX = 0, newY = 0, origX = 0, origY = 0;
                if ((mouseEvent.getY() >= observationSide / 2) && (mouseEvent.getX() >= observationSide / 2)) {
                    newX = mouseEvent.getX();
                    newY = mouseEvent.getY();

                    origX = observation.getOrigin().getX();
                    origY = observation.getOrigin().getY();

                    observation.getObservation().setX(newX - observationSide / 2);
                    observation.getObservation().setY(newY - observationSide / 2);
                    observation.getOrigin().setX(newX);
                    observation.getOrigin().setY(newY);
                    observation.updateObservationNamePosition();

                    for (State s : currentChain.getStates()) {

                        makeDraggableState.renewTransitionsObservation(s, observation, currentChain, mainDrawingPane, newX - origX, newY - origY);

                    }


                }
            }
        });
    }

    public void selectObservation(Observation observation) {
        for (State st : currentChain.getStates()) {
            for (Transition t : st.getTransitions()) {
                if (t.getObsTo() == observation) {
                    mainDrawingPane.getChildren().removeAll(t.getCurve().getCnt1(), t.getCurve().getCnt2(), t.getCurve().lb1, t.getCurve().lb2);
                    t.getCurve().selectionMode(true);
                }

            }
            removeDragClickListeners(st);
            colorState(st, Color.BLACK);
            st.getState().setStroke(Color.BLACK);
        }

        mainDrawingPane.getChildren().removeAll(observation.getObservation(), observation.getObservationName());
        observation.getObservation().setStroke(Color.DARKGRAY);
        observation.getObservationName().setTextFill(Color.DARKGRAY);
        enableObservationDrag(observation);
        mainDrawingPane.getChildren().addAll(observation.getObservation(), observation.getObservationName());
    }

    public void deselectObservation(Observation observation) {
        observation.getObservation().setStroke(Color.BLACK);
        observation.getObservationName().setTextFill(Color.BLACK);
        observation.getObservation().setOnMouseDragged(null);
    }

    public void enableObservationOnClick(Observation observation) {
        observation.getObservation().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (currentOption == Option.SELECT) {

                    currentlySelectedStates.removeAll(currentlySelectedStates);
                    if (currentlySelectedObservation != null) {

                        if (currentlySelectedObservation != observation) {
                            for (State st : currentChain.getStates()) {
                                for (Transition t : st.getTransitions()) {
                                    mainDrawingPane.getChildren().removeAll(t.getCurve().getCnt1(), t.getCurve().getCnt2(), t.getCurve().lb1, t.getCurve().lb2);
                                    t.getCurve().selectionMode(false);

                                }
                                removeDragClickListeners(st);
                                colorState(st, Color.BLACK);
                                st.getState().setStroke(Color.BLACK);
                            }
                            deselectObservation(currentlySelectedObservation);
                            currentlySelectedObservation = observation;
                            selectObservation(observation);
                        } else {
                            currentlySelectedObservation.getObservation().setStroke(Color.DARKGRAY);
                            currentlySelectedObservation.getObservationName().setTextFill(Color.DARKGRAY);

                            for (State s : currentChain.getStates()) {
                                for (Transition t : s.getTransitions()) {

                                    if (t.getObsTo() == currentlySelectedObservation)
                                        if (mainDrawingPane.getChildren().contains(t.getCurve().getCnt1())) {
                                            mainDrawingPane.getChildren().removeAll(t.getCurve().getCnt1(), t.getCurve().getCnt2(), t.getCurve().lb1, t.getCurve().lb2);

                                        }
                                }


                            }


                        }

                    } else {
                        for (State st : currentChain.getStates()) {
                            for (Transition t : st.getTransitions()) {
                                mainDrawingPane.getChildren().removeAll(t.getCurve().getCnt1(), t.getCurve().getCnt2(), t.getCurve().lb1, t.getCurve().lb2);
                                t.getCurve().selectionMode(false);

                            }
                            removeDragClickListeners(st);
                            colorState(st, Color.BLACK);
                            st.getState().setStroke(Color.BLACK);
                        }
                        currentlySelectedObservation = observation;
                        selectObservation(observation);
                    }
                }
            }
        });

    }


    public List<State> getTransitionsTo(State state) {
        List<State> statesToReturn = new ArrayList<>();
        for (State s : currentChain.getStates()) {
            for (Transition t : s.getTransitions()) {
                if (t.getTo() == state) {
                    statesToReturn.add(t.getFrom());
                }
            }
        }
        return statesToReturn;
    }

    public List<State> customBfs(State state) {
        List<State> states = new ArrayList<>();
        Queue<State> stateQueue = new LinkedList<>();
        stateQueue.add(state);


        while (!stateQueue.isEmpty()) {
            State s = stateQueue.remove();
            if (!states.contains(s))
                states.add(s);

            List<State> enteringStates = getTransitionsTo(s);
            if (enteringStates != null)
                for (State st : enteringStates)
                    if (!states.contains(st))
                        stateQueue.add(st);


            for (Transition t : s.getTransitions()) {
                if (t.getObsTo() == null) {
                    if (!states.contains(t.getTo()))
                        stateQueue.add(t.getTo());
                }
            }
        }

        return states;

    }


    public boolean colorValidStates(List<State> states) {
        boolean validChain = true;
        HashMap<String, State> nameHash = new HashMap<>();
        for (State state : states) {

            double sum = 0;
            for (Transition t : state.getTransitions()) {

                if (t.getObsTo() == null) {
                    sum = addTwoDoubles(sum, t.getProbability());
                }
            }
            if (sum < 0.99 || sum > 1.0000000000000010) {
                colorStateAndTransitions(state, Color.RED);
                validChain = false;
            } else
                colorStateAndTransitions(state, Color.GREEN);

            if (!nameHash.isEmpty()) {
                if (nameHash.containsKey(state.getStateName().getText())) {
                    colorState(state, Color.RED);
                    colorState(nameHash.get(state.getStateName().getText()), Color.RED);
                    validChain = false;
                }
            }
            nameHash.put(state.getStateName().getText(), state);
        }
        return validChain;
    }

    public void colorState(State s, Color color) {
        s.getStateName().setTextFill(color);
    }

    public void colorStateAndTransitions(State s, Color color) {
        for (Transition t : s.getTransitions()) {
            if (t.getObsTo() == null) {
                t.getCurve().getCubicCurve().setStroke(color);
                t.getCurve().getArrow().setStroke(color);
                t.getCurve().getArrow().setFill(color);
                t.getCurve().getProbabilityLabel().setTextFill(color);
            }
        }
        s.getState().setStroke(color);
        s.getStateName().setTextFill(color);
    }

    public void colorStateAndTransitionsForObservation(Observation o, Color color) {
        for (State s : o.getStates()) {
            for (Transition t : s.getTransitions()) {
                if (t.getObsTo() == o) {
                    t.getCurve().getCubicCurve().setStroke(color);
                    t.getCurve().getArrow().setStroke(color);
                    t.getCurve().getArrow().setFill(color);
                }
            }
        }
        o.getObservation().setStroke(color);
        o.getObservationName().setTextFill(color);
    }


    public void validateChain(MouseEvent mouseEvent) {
        State selectedState = null;
        boolean valid = true;
        for (State s : currentChain.getStates()) {
            if (getDistanceBetweenTwoPoints(mouseEvent.getX(), mouseEvent.getY(), s.getState().getCenterX(), s.getState().getCenterY()) < circleRadius) {
                selectedState = s;
                break;
            }
        }


        if (selectedState != null) {
            List<State> states = customBfs(selectedState);

            boolean cont = false;
            if (multipleSelectStates != null) {
                for (State f : states) {
                    if (!multipleSelectStates.contains(f)) {

                        cont = true;
                    }
                }
            } else
                cont = true;

            if (cont) {
                if (multipleSelectStates != null) {
                    for (State st : multipleSelectStates) {

                        colorStateAndTransitions(st, Color.BLACK);
                        for (Transition t : st.getTransitions()) {
                            if (t.getObsTo() != null) {
                                t.getCurve().getCubicCurve().setStroke(Color.BLACK);
                                t.getCurve().getArrow().setFill(Color.BLACK);
                                t.getCurve().getArrow().setStroke(Color.BLACK);
                                t.getObsTo().getObservationName().setTextFill(Color.BLACK);
                                t.getObsTo().getObservation().setStroke(Color.BLACK);
                                t.getCurve().getProbabilityLabel().setTextFill(Color.BLACK);
                            }
                        }
                        removeDragClickListeners(st);

                    }

                }


                if (states != null) {
                    if (colorValidStates(states))
                        incidenceMatrixChain = getIncidenceMatrixFromSelection(states);
                    multipleSelectStates = states;
                }

                colorValidObservations(states);


                MakeDraggableGroup makeDraggableGroup = new MakeDraggableGroup(mainDrawingPane, circleRadius, incidenceMatrixChain);

                for (State state : states) {

                    removeDragClickListeners(state);
                    mainDrawingPane.getChildren().remove(state.getTestLine());
                    makeDraggableGroup.enableDrag(state.getState(), state, currentChain, states);

                }


            }


        }


    }

    public void colorStateAndTransitionsProbabilitiesForObservation(State state, Color color) {

        for (Transition t : state.getTransitions()) {
            if (t.getObsTo() != null) {
                t.getCurve().getProbabilityLabel().setTextFill(color);
            }

        }
    }


    public void colorValidObservations(List<State> states) {
        Observation biggestObservation = null;
        double size = 0;

        for (State s : states) {
            double sum = 0;
            for (Transition t : s.getTransitions()) {
                boolean cont = true;
                if (t.getObsTo() != null) {

                    for (State s1 : states) {
                        if (!t.getObsTo().getStates().contains(s1)) {
                            colorStateAndTransitionsForObservation(t.getObsTo(), Color.RED);
                            cont = false;

                            break;
                        }

                    }

                    if (cont) {
                        colorStateAndTransitionsForObservation(t.getObsTo(), Color.GREEN);

                    }
                }
            }


        }

        List<Observation> observations = new ArrayList<>();

        HashMap<String, Observation> hashMap = new HashMap<>();

        for (State s : states) {
            double sum = 0;
            for (Transition t : s.getTransitions()) {
                if (t.getObsTo() != null) {
                    sum = addTwoDoubles(sum, t.getProbability());
                    if (!hashMap.containsKey(t.getObsTo().getObservationName().getText())) {
                        hashMap.put(t.getObsTo().getObservationName().getText(), t.getObsTo());

                    }
                    if (!observations.contains(t.getObsTo()))
                        observations.add(t.getObsTo());

                }
            }


            if (sum < 0.9999999999999999 || sum > 1.0000000000000010) {

                colorStateAndTransitionsProbabilitiesForObservation(s, Color.RED);

            } else {

                colorStateAndTransitionsProbabilitiesForObservation(s, Color.GREEN);
            }

        }

        for (Observation obs : observations) {
            if (hashMap.containsKey(obs.getObservationName().getText())) {
                if (obs != hashMap.get(obs.getObservationName().getText())) {
                    obs.getObservationName().setTextFill(Color.RED);
                    hashMap.get(obs.getObservationName().getText()).getObservationName().setTextFill(Color.RED);
                }

            }
        }


    }


    public void selectStates(MouseEvent mouseEvent) {
        for (State s : currentChain.getStates()) {
            if (getDistanceBetweenTwoPoints(mouseEvent.getX(), mouseEvent.getY(), s.getState().getCenterX(), s.getState().getCenterY()) < circleRadius) {
                if (currentlySelectedStates.size() == 1) {
                    removeDragClickListeners(currentlySelectedStates.get(0));
                    colorSelectedState(currentlySelectedStates.get(0), Color.BLACK);
                    currentlySelectedStates.remove(currentlySelectedStates.get(0));
                }
                colorSelectedState(s, Color.DARKGRAY);
                currentlySelectedStates.add(s);
                makeDraggableState = new MakeDraggableState(mainDrawingPane.getWidth(), mainDrawingPane.getHeight(), circleRadius);
                makeDraggableState.enableDrag(s.getState(), s, currentChain, mainDrawingPane);
                //removeTestLinesAndDragListenersForCurrentlySelected();

                if (currentlySelectedTransitions.size() != 0) {
                    for (Transition t : currentlySelectedTransitions) {
                        mainDrawingPane.getChildren().removeAll(t.getCurve().lb2, t.getCurve().lb1, t.getCurve().getCnt1(), t.getCurve().getCnt2());
                        t.getCurve().selectionMode(false);
                    }
                }


                for (State f : currentChain.getStates()) {
                    for (Transition t : f.getTransitions()) {
                        if (t.getTo() == s) {
                            t.getCurve().selectionMode(true);
                        } else
                            t.getCurve().selectionMode(false);
                    }
                }

                for (Transition t : s.getTransitions()) {
                    if ((t.getFrom() == s) || (t.getTo() == s)) {
                        t.getCurve().selectionMode(true);
                    }

                }

                if (currentlySelectedObservation != null) {
                    deselectObservation(currentlySelectedObservation);

                    currentlySelectedObservation = null;
                }


            }


        }


    }

    public void removeStatesFromPane(List<State> states) {
        List<Observation> auxObs = new ArrayList<>();
        for (State s : states) {
            mainDrawingPane.getChildren().removeAll(s.getTestLine(), s.getStateName(), s.getState());
            for (Transition t : s.getTransitions()) {
                mainDrawingPane.getChildren().removeAll(t.getCnt1(), t.getCnt2(), t.getCurve().getCubicCurve(), t.getCurve().getProbabilityLabel(), t.getCurve().getArrow());
                if (t.getObsTo() != null) {
                    mainDrawingPane.getChildren().removeAll(t.getObsTo().getObservationName(), t.getObsTo().getObservation());
                    auxObs.add(t.getObsTo());
                }
            }
            currentChain.getStates().remove(s);
            currentChain.getObservations().removeAll(auxObs);


        }


    }

    public void RemoveCurrentlySelectedStates() {
        List<Transition> auxTransitions = new ArrayList<>();
        for (State s : currentlySelectedStates) {
            mainDrawingPane.getChildren().remove(s.getState());
            mainDrawingPane.getChildren().remove(s.getTestLine());
            for (Transition t : s.getTransitions()) {
                mainDrawingPane.getChildren().remove(t.getCubicCurve());
                mainDrawingPane.getChildren().remove(t.getCurve().getCnt1());
                mainDrawingPane.getChildren().remove(t.getCurve().getCnt2());
                mainDrawingPane.getChildren().removeAll(t.getCurve().lb2, t.getCurve().lb1, t.getCurve().getArrow(), t.getCurve().getProbabilityLabel());
            }

            for (State s1 : currentChain.getStates()) {
                for (Transition t : s1.getTransitions()) {
                    if (t.getTo() == s) {
                        auxTransitions.add(t);
                        mainDrawingPane.getChildren().remove(t.getCubicCurve());
                        mainDrawingPane.getChildren().remove(t.getCurve().getCnt1());
                        mainDrawingPane.getChildren().remove(t.getCurve().getCnt2());
                        mainDrawingPane.getChildren().removeAll(t.getCurve().lb2, t.getCurve().lb1, t.getCurve().getArrow(), t.getCurve().getProbabilityLabel());
                    }
                }
            }

            mainDrawingPane.getChildren().remove(s.getStateName());
            currentChain.getStates().remove(s);

        }
        for (Transition t : auxTransitions) {
            for (State s : currentChain.getStates()) {
                if (s.getTransitions().contains(t))
                    s.getTransitions().remove(t);
            }
        }

        currentlySelectedStates = new ArrayList<>();


        auxTransitions = new ArrayList<>();
        for (State s : currentChain.getStates()) {
            for (Transition t : s.getTransitions()) {
                if (t.getCurve().getCubicCurve().getStroke().toString().contains("0xa9a9a9ff")) {
                    auxTransitions.add(t);
                    mainDrawingPane.getChildren().remove(t.getCubicCurve());
                    mainDrawingPane.getChildren().remove(t.getCurve().getCnt1());
                    mainDrawingPane.getChildren().remove(t.getCurve().getCnt2());
                    mainDrawingPane.getChildren().removeAll(t.getCurve().lb2, t.getCurve().lb1, t.getCurve().getArrow(), t.getCurve().getProbabilityLabel());
                }
            }

        }
        for (Transition t : auxTransitions) {
            for (State s : currentChain.getStates()) {
                if (s.getTransitions().contains(t)) {
                    if (t.getObsTo() != null)
                        t.getObsTo().getStates().remove(t.getFrom());
                    s.getTransitions().remove(t);
                }
            }
        }

        if (multipleSelectStates != null) {

            removeStatesFromPane(multipleSelectStates);
        }
        multipleSelectStates = null;

        if (currentlySelectedObservation != null) {


            if (currentlySelectedObservation.getObservation().getStroke().toString().contains("0xa9a9a9ff")) {


                removeObservation(currentlySelectedObservation);
            }

        }


    }

    public void removeObservation(Observation observation) {

        mainDrawingPane.getChildren().removeAll(observation.getObservation(), observation.getObservationName());
        currentChain.getObservations().remove(observation);
    }

    public double addTwoDoubles(double x, double y) {
        BigDecimal bigDecimalx = new BigDecimal(x);
        BigDecimal bigDecimaly = new BigDecimal(y);
        bigDecimalx = bigDecimalx.add(bigDecimaly);
        return bigDecimalx.doubleValue();
    }

    public Circle constructCircle(double x, double y, double radius) {
        Circle circle = new Circle(x, y, radius);
        circle.setFill(Color.WHITE);
        circle.setStrokeWidth(lineWidth);
        circle.setStroke(Color.BLACK);
        return circle;
    }

    public static double getDistanceBetweenTwoPoints(double x1, double y1, double x2, double y2) {
        return Math.sqrt((Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));
    }

    public List<Double> getInBounds(double x, double y, double radius) {
        double newx = x, newy = y;
        List<Double> newCoords = new ArrayList<>();
        if (y + radius > mainDrawingPane.getHeight()) {
            newy = mainDrawingPane.getHeight() - radius;
        }
        if (y - radius < 0) {
            newy = radius;
        }
        if (x + radius > mainDrawingPane.getWidth()) {
            newx = mainDrawingPane.getWidth() - radius;
        }
        if (x - radius < 0) {
            newx = radius;
        }
        newCoords.add(newx);
        newCoords.add(newy);
        return newCoords;

    }

    private boolean checkStateOverlap(double x, double y, double radius) {

        for (State s : currentChain.getStates()) {
            if (getDistanceBetweenTwoPoints(x, y, s.getState().getCenterX(), s.getState().getCenterY()) < 2 * radius) {
                return false;
            }
        }
        for (Observation observation : currentChain.getObservations()) {
            if (getDistanceBetweenTwoPoints(x, y, observation.getOrigin().getX(), observation.getOrigin().getY()) < 1.2 * observationSide) {
                return false;
            }
        }
        return true;

    }

    public void disableStateNameOnCick() {
        StateNameHandler stateNameHandler = new StateNameHandler();
        for (State s : currentChain.getStates()) {
            stateNameHandler.disableStateNameOnClick(s, currentChain.getStates());
        }
    }

    public void enableStateNameOnClick() {
        StateNameHandler stateNameHandler = new StateNameHandler();
        for (State s : currentChain.getStates()) {
            stateNameHandler.enableStateNameOnClick(s, currentChain.getStates());
        }
    }

    public void drawState(MouseEvent mouseEvent) {
        List<Double> coords = getInBounds(mouseEvent.getX(), mouseEvent.getY(), circleRadius + 3);
        if (checkStateOverlap(coords.get(0), coords.get(1), circleRadius)) {
            StateNameHandler stateNameHandler = new StateNameHandler();
            State state = new State(constructCircle(coords.get(0), coords.get(1), circleRadius), currentChain);
            stateNameHandler.enableStateNameOnClick(state, currentChain.getStates());
            currentChain.getStates().add(state);
            mainDrawingPane.getChildren().addAll(state.getState(), state.getTestLine(), state.getStateName());
        }
    }

    public void openProbabilityMatrix() {
        ProbabilityMatrixHandler probabilityMatrixHandler = new ProbabilityMatrixHandler();
        probabilityMatrixHandler.openProbabilityChainMatrix(currentChain, mainDrawingPane, currentlySelectedStates, currentlySelectedTransitions);
    }


    public IncidenceMatrixChain getIncidenceMatrixFromSelection(List<State> states) {
        double[][] matrix = new double[0][];
        HashMap<String, Integer> stateOrder = new HashMap<>();
        TreeMap<Integer, String> stateOrderTree = new TreeMap<>();
        int stateCount = 0;
        int n = 0;

        n = states.size();

        matrix = new double[n][n];
        for (State s : states) {
            stateOrder.put(s.getStateName().getText(), stateCount);
            stateOrderTree.put(stateCount, s.getStateName().getText());
            stateCount += 1;
        }
        for (State s : states) {
            for (Transition t : s.getTransitions()) {
                if (t.getObsTo() == null) {
                    matrix[stateOrder.get(t.getFrom().getStateName().getText())][stateOrder.get(t.getTo().getStateName().getText())] = t.getProbability();
                }
            }
        }
        IncidenceMatrixChain incidenceMatrixChain = new IncidenceMatrixChain(stateOrder, stateOrderTree, stateCount, matrix);


        return incidenceMatrixChain;

    }


    public void drawTransition(MouseEvent mouseEvent) {


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
        if (dst == -1)
            minDist = 0;

        Transition t = new Transition(s, s, -1, new Line(x, 0, 0, y));

        if ((x - selfTransitionSize > 0) && (y - selfTransitionSize > 0)) {

            double newDist = 0;
            newDist += getDistanceBetweenTwoPoints((x + x - selfTransitionSize) / 2, (y - selfTransitionSize + y) / 2, minDistX, minDistY);
            if (newDist > minDist) {
                t = new Transition(s, s, -1, new Line(x, y - circleRadius, x - circleRadius, y));
                t.getCubicCurve().setControlX1(x);
                t.getCubicCurve().setControlY1(y - selfTransitionSize);
                t.getCubicCurve().setControlX2(x - selfTransitionSize);
                t.getCubicCurve().setControlY2(y);
                minDist = newDist;
            }

        }
        if ((x + selfTransitionSize < mainDrawingPane.getWidth()) && (y - selfTransitionSize > 0)) {

            double newDist = 0;
            newDist += getDistanceBetweenTwoPoints((x + x + selfTransitionSize) / 2, (y - selfTransitionSize + y) / 2, minDistX, minDistY);
            if (newDist > minDist) {
                t = new Transition(s, s, -1, new Line(x, y - circleRadius, x + circleRadius, y));
                t.getCubicCurve().setControlX1(x);
                t.getCubicCurve().setControlY1(y - selfTransitionSize);
                t.getCubicCurve().setControlX2(x + selfTransitionSize);
                t.getCubicCurve().setControlY2(y);
                minDist = newDist;
            }

        }
        if ((x - selfTransitionSize > 0) && (y + selfTransitionSize > 0)) {
            double newDist = 0;
            newDist += getDistanceBetweenTwoPoints((x + x - selfTransitionSize) / 2, (y + selfTransitionSize + y) / 2, minDistX, minDistY);
            if (newDist > minDist) {
                t = new Transition(s, s, -1, new Line(x, y + circleRadius, x - circleRadius, y));
                t.getCubicCurve().setControlX1(x);
                t.getCubicCurve().setControlY1(y + selfTransitionSize);
                t.getCubicCurve().setControlX2(x - selfTransitionSize);
                t.getCubicCurve().setControlY2(y);
                minDist = newDist;
            }

        }
        if ((x + selfTransitionSize > 0) && (y + selfTransitionSize > 0)) {

            double newDist = 0;

            newDist += getDistanceBetweenTwoPoints((x + x + selfTransitionSize) / 2, (y + selfTransitionSize + y) / 2, minDistX, minDistY);

            if (newDist > minDist) {
                t = new Transition(s, s, -1, new Line(x, y + circleRadius, x + circleRadius, y));
                t.getCubicCurve().setControlX1(x);
                t.getCubicCurve().setControlY1(y + selfTransitionSize);
                t.getCubicCurve().setControlX2(x + selfTransitionSize);
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
        curveDragPoints.enableDragPoints(t.getCurve(), t, currentChain.getStates(), currentlySelectedTransitions, currentlySelectedStates, currentChain);
        //probabilityHandler.enableOnClickProbability(t);
        t.getCurve().selectionMode(false);


        t.setProbability(probability);
        t.getCurve().getProbabilityLabel().setText(Double.toString(probability));


    }

    public void circularTransition(MouseEvent mouseEvent) {

        for (State s : currentlySelectedStates) {
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

            if ((x - selfTransitionSize > 0) && (y - selfTransitionSize > 0)) {

                double newDist = 0;
                newDist += getDistanceBetweenTwoPoints((x + x - selfTransitionSize) / 2, (y - selfTransitionSize + y) / 2, minDistX, minDistY);
                if (newDist > minDist) {
                    t = new Transition(s, s, -1, new Line(x, y - circleRadius, x - circleRadius, y));
                    t.getCubicCurve().setControlX1(x);
                    t.getCubicCurve().setControlY1(y - selfTransitionSize);
                    t.getCubicCurve().setControlX2(x - selfTransitionSize);
                    t.getCubicCurve().setControlY2(y);
                    minDist = newDist;
                }

            }
            if ((x + selfTransitionSize < mainDrawingPane.getWidth()) && (y - selfTransitionSize > 0)) {

                double newDist = 0;
                newDist += getDistanceBetweenTwoPoints((x + x + selfTransitionSize) / 2, (y - selfTransitionSize + y) / 2, minDistX, minDistY);
                if (newDist > minDist) {
                    t = new Transition(s, s, -1, new Line(x, y - circleRadius, x + circleRadius, y));
                    t.getCubicCurve().setControlX1(x);
                    t.getCubicCurve().setControlY1(y - selfTransitionSize);
                    t.getCubicCurve().setControlX2(x + selfTransitionSize);
                    t.getCubicCurve().setControlY2(y);
                    minDist = newDist;
                }

            }
            if ((x - selfTransitionSize > 0) && (y + selfTransitionSize > 0)) {
                double newDist = 0;
                newDist += getDistanceBetweenTwoPoints((x + x - selfTransitionSize) / 2, (y + MainController.selfTransitionSize + y) / 2, minDistX, minDistY);
                if (newDist > minDist) {
                    t = new Transition(s, s, -1, new Line(x, y + circleRadius, x - circleRadius, y));
                    t.getCubicCurve().setControlX1(x);
                    t.getCubicCurve().setControlY1(y + selfTransitionSize);
                    t.getCubicCurve().setControlX2(x - selfTransitionSize);
                    t.getCubicCurve().setControlY2(y);
                    minDist = newDist;
                }

            }
            if ((x + selfTransitionSize > 0) && (y + selfTransitionSize > 0)) {

                double newDist = 0;

                newDist += getDistanceBetweenTwoPoints((x + x + selfTransitionSize) / 2, (y + selfTransitionSize + y) / 2, minDistX, minDistY);
                if (newDist > minDist) {
                    t = new Transition(s, s, -1, new Line(x, y + circleRadius, x + circleRadius, y));
                    t.getCubicCurve().setControlX1(x);
                    t.getCubicCurve().setControlY1(y + selfTransitionSize);
                    t.getCubicCurve().setControlX2(x + selfTransitionSize);
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
            curveDragPoints.enableDragPoints(t.getCurve(), t, currentChain.getStates(), currentlySelectedTransitions, currentlySelectedStates, currentChain);
            //probabilityHandler.enableOnClickProbability(t);
            t.getCurve().selectionMode(true);
            s.updateStateNamePosition();


        }
    }

    public void ToggleArrowCross(MouseEvent mouseEvent) {
        if (mainScrollPane.isPannable())
            mainScrollPane.setPannable(false);
        else
            mainScrollPane.setPannable(true);

    }


    public void expandPane(MouseEvent mouseEvent) {


        this.mainDrawingPane.setPrefHeight(mainDrawingPane.getHeight() + 100);
        this.mainDrawingPane.setPrefWidth(mainDrawingPane.getWidth() + 100);
        didOnce = false;

        /*
        this.mainScrollPane.setPrefHeight(mainScrollPane.getHeight()+100);
        this.mainScrollPane.setPrefWidth(mainScrollPane.getWidth()+100);

         */


    }


    public void enableMultipleSelect(MouseEvent mouseEvent) {


        if (currentOption == Option.MULTIPLESELECT) {
            resetHighlight();
            disableSelectionMode();

            setCurrentOption(Option.NONE);

        } else {
            disableSelectionMode();
            resetHighlight();
            setCurrentOption(Option.MULTIPLESELECT);
            highlightOption();
            disableProbabilityListeners();

        }

        if ((!validationMode)) {
            validationMode = true;
            disableSelectionMode();
        } else {
            disableValidationMode();
            resetHighlight();

        }
        disableStateNameOnCick();

    }

    public void disableValidationMode() {
        validationMode = false;
        if (multipleSelectStates != null) {
            for (State st : multipleSelectStates) {
                colorStateAndTransitions(st, Color.BLACK);
                removeDragClickListeners(st);

            }

            multipleSelectStates = null;
        }
        disableSelectionMode();


    }

    public void resetBoard() {
        disableSelectionMode();


        for (State s : currentChain.getStates()) {
            mainDrawingPane.getChildren().removeAll(s.getTestLine(), s.getStateName(), s.getState());
            for (Transition t : s.getTransitions()) {
                mainDrawingPane.getChildren().removeAll(t.getCnt1(), t.getCnt2(), t.getCurve().getCubicCurve(), t.getCurve().getProbabilityLabel(), t.getCurve().getArrow());
            }
        }

        nrOfStates = 0;
        nrOfObservations = 0;

        mainScrollPane.getTransforms().removeAll(mainScrollPane.getTransforms());
        viewScale = 1;


        mainDrawingPane.setPrefWidth(fatherPane.getWidth());
        mainDrawingPane.setPrefHeight(fatherPane.getHeight());
        mainScrollPane.setPrefHeight(fatherPane.getHeight());
        mainScrollPane.setPrefWidth(fatherPane.getWidth());
        if ((mainBorderPane.getWidth() < screenWidth) && (mainBorderPane.getHeight() < screenHeight)) {
            resized = false;
        }
        didOnce = false;


        for (Observation o : currentChain.getObservations()) {
            mainDrawingPane.getChildren().removeAll(o.getObservation(), o.getObservationName());
        }
        currentChain = new Chain();
        resetHighlight();

    }

    public void setResizeListeners() {
        fatherPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                mainScrollPane.setPrefWidth(fatherPane.getWidth() * (1 / viewScale));
                mainScrollPane.setPrefHeight(fatherPane.getHeight() * (1 / viewScale));

                if (viewScale != 1) {
                    if (resized == false) {

                        mainDrawingPane.setPrefWidth(fatherPane.getWidth());
                        mainDrawingPane.setPrefWidth(mainDrawingPane.getWidth() * (2));
                        mainDrawingPane.setPrefHeight(fatherPane.getHeight());
                        mainDrawingPane.setPrefHeight(mainDrawingPane.getHeight() * (2));

                        resized = true;
                    }
                }
            }
        });
        fatherPane.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                mainScrollPane.setPrefHeight(fatherPane.getHeight() * (1 / viewScale));
                if (viewScale != 1) {
                    if (resized == false) {
                        mainDrawingPane.setPrefHeight(fatherPane.getHeight());
                        mainDrawingPane.setPrefHeight(mainDrawingPane.getHeight() * (2));
                        mainDrawingPane.setPrefWidth(fatherPane.getWidth());
                        mainDrawingPane.setPrefWidth(mainDrawingPane.getWidth() * (2));
                        resized = true;
                    }
                }
            }
        });
    }


    public void changeScale() {
        Scale scale = new Scale();
        scale.setY(viewScale);
        scale.setX(viewScale);
        scale.setPivotY(0);
        scale.setPivotX(0);
        mainScrollPane.getTransforms().removeAll(mainScrollPane.getTransforms());
        mainScrollPane.getTransforms().add(scale);
        mainScrollPane.setPrefWidth(fatherPane.getWidth() * (1 / viewScale));
        mainScrollPane.setPrefHeight(fatherPane.getHeight() * (1 / viewScale));
        if (viewScale != 1) {
            if (didOnce == false) {
                mainDrawingPane.setPrefWidth(mainDrawingPane.getWidth() * (2));
                mainDrawingPane.setPrefHeight(mainDrawingPane.getHeight() * (2));
                didOnce = true;
            }
        }


    }

    public void resetVCheck() {
        v50.setSelected(false);
        v75.setSelected(false);
        v100.setSelected(false);
    }

    public void resetSO() {
        soBi.setSelected(false);
        soMe.setSelected(false);
        soSm.setSelected(false);
    }

    public void resetLn() {
        lBi.setSelected(false);
        lMe.setSelected(false);
        lSm.setSelected(false);
    }

    public void view50() {

        if (viewScale != 0.5) {
            viewScale = 1;
            changeScale();
            viewScale = 0.5;
            setResizeListeners();
            changeScale();
        }
        resetVCheck();
        v50.setSelected(true);


    }

    public void view75() {
        if (viewScale != 0.75) {
            viewScale = 1;
            changeScale();
            viewScale = 0.75;
            setResizeListeners();
            changeScale();
        }
        resetVCheck();
        v75.setSelected(true);
    }

    public void view100() {
        if (viewScale != 1) {
            viewScale = 1;
            setResizeListeners();
            changeScale();
        }
        resetVCheck();
        v100.setSelected(true);
    }


    public void view50(ActionEvent actionEvent) {

        if (viewScale != 0.5) {
            viewScale = 1;
            changeScale();
            viewScale = 0.5;
            setResizeListeners();
            changeScale();
        }
        resetVCheck();
        v50.setSelected(true);


    }

    public void view75(ActionEvent actionEvent) {
        if (viewScale != 0.75) {
            viewScale = 1;
            changeScale();
            viewScale = 0.75;
            setResizeListeners();
            changeScale();
        }
        resetVCheck();
        v75.setSelected(true);
    }

    public void view100(ActionEvent actionEvent) {
        if (viewScale != 1) {
            viewScale = 1;
            setResizeListeners();
            changeScale();
        }
        resetVCheck();
        v100.setSelected(true);
    }

    public void view125(ActionEvent actionEvent) {


        viewScale = 1.25;
        setResizeListeners();
        changeScale();

    }

    public void view150(ActionEvent actionEvent) {

        viewScale = 1.5;
        setResizeListeners();
        changeScale();

    }

    public void saveProject(ActionEvent actionEvent) throws IOException {
        SerializableChain serializableChain = new SerializableChain();
        for (State s : currentChain.getStates()) {
            SerializableState serializableState = new SerializableState(s.getStateName().getText(), new Point(s.getState().getCenterX(), s.getState().getCenterY()));
            serializableChain.getStates().add(serializableState);
            for (Transition t : s.getTransitions()) {
                String from = t.getFrom().getStateName().getText() + t.getFrom().getState().getCenterX() + t.getFrom().getState().getCenterY();
                String to;
                if (t.getObsTo() == null)
                    to = t.getTo().getStateName().getText() + t.getTo().getState().getCenterX() + t.getTo().getState().getCenterY();
                else
                    to = t.getObsTo().getObservationName().getText() + t.getObsTo().getOrigin().getX() + t.getObsTo().getOrigin().getY();
                List<Point> curvePoints = new ArrayList<>();
                curvePoints.add(new Point(t.getCurve().getCubicCurve().getStartX(), t.getCubicCurve().getStartY()));
                curvePoints.add(new Point(t.getCurve().getCubicCurve().getControlX1(), t.getCubicCurve().getControlY1()));
                curvePoints.add(new Point(t.getCurve().getCubicCurve().getControlX2(), t.getCubicCurve().getControlY2()));
                curvePoints.add(new Point(t.getCurve().getCubicCurve().getEndX(), t.getCubicCurve().getEndY()));
                List<Point> dragPoints = new ArrayList<>();
                dragPoints.add(new Point(t.getCurve().getCnt1().getCenterX(), t.getCurve().getCnt1().getCenterY()));
                dragPoints.add(new Point(t.getCurve().getCnt2().getCenterX(), t.getCurve().getCnt2().getCenterY()));

                SerializableTransition serializableTransition = new SerializableTransition(from, to, curvePoints, dragPoints, t.getProbability(), t.getCurve().isLineMode());
                serializableChain.getTransitions().add(serializableTransition);
            }
        }
        for (Observation o : currentChain.getObservations()) {
            SerializableObservation serializableObservation = new SerializableObservation(o.getObservationName().getText(), new Point(o.getOrigin().getX(), o.getOrigin().getY()));
            serializableChain.getObservations().add(serializableObservation);
        }

        serializableChain.setScreenWidth(mainDrawingPane.getWidth());
        serializableChain.setScreenHeight(mainDrawingPane.getHeight());


        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("project.ser");
        fileChooser.setTitle("Save");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("SER", "SER", "*.ser"));

        try {
            File selectedFile = fileChooser.showSaveDialog(mainScrollPane.getScene().getWindow());
            if (selectedFile.exists() && !selectedFile.isDirectory()) {
                try {

                    FileOutputStream fileOutputStream = new FileOutputStream(selectedFile);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    objectOutputStream.writeObject(serializableChain);
                    objectOutputStream.close();
                    /*
                    fileOutputStream.close();
                    FileInputStream fileInputStream=new FileInputStream("save.ser");
                    openProject(fileInputStream);

                     */

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            } else {
                File f = new File(selectedFile.getAbsolutePath());
                FileOutputStream fileOutputStream = new FileOutputStream(f);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(serializableChain);
                objectOutputStream.close();

            }

        } catch (NullPointerException e) {

        }


    }

    public void openProject(FileInputStream fileInputStream) {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            SerializableChain serializableChain = (SerializableChain) objectInputStream.readObject();
            drawProjectFromSerializableClass(serializableChain);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    public void drawProjectFromSerializableClass(SerializableChain serializableChain) {
        resetBoard();
        resetHighlight();
        HashMap<String, State> stateHashMap = new HashMap<>();
        HashMap<String, Observation> observationHashMap = new HashMap<>();
        mainDrawingPane.setPrefWidth(serializableChain.getScreenWidth());
        mainDrawingPane.setPrefHeight(serializableChain.getScreenHeight());

        for (SerializableState serializableState : serializableChain.getStates()) {

            State newState = makeState(serializableState.getPosition().getX(), serializableState.getPosition().getY(), serializableState.getName());
            currentChain.getStates().add(newState);
            stateHashMap.put(serializableState.getKey(), newState);

        }
        for (SerializableObservation serializableObservation : serializableChain.getObservations()) {
            Observation newObservation = new Observation(serializableObservation.getPosition().getX(), serializableObservation.getPosition().getY(), currentChain);
            newObservation.getObservationName().setText(serializableObservation.getName());
            newObservation.updateObservationNamePosition();

            currentChain.getObservations().add(newObservation);
            observationHashMap.put(serializableObservation.getKey(), newObservation);

        }

        for (SerializableTransition serializableTransition : serializableChain.getTransitions()) {
            String from = serializableTransition.getFrom();
            String to = serializableTransition.getTo();
            List<Point> curvePoints = serializableTransition.getCurvePoints();
            List<Point> dragPoints = serializableTransition.getDragPoints();
            Transition newTransition;
            if (stateHashMap.containsKey(to) == true) {
                LineDistanceManipulator lineDistanceManipulator = new LineDistanceManipulator();
                Line transitionLine = lineDistanceManipulator.getLineAlteredFromBeginning(stateHashMap.get(from).getState().getCenterX(), stateHashMap.get(from).getState().getCenterY(), stateHashMap.get(to).getState().getCenterX(), stateHashMap.get(to).getState().getCenterY(), MainController.circleRadius);
                transitionLine = lineDistanceManipulator.getLineAlteredFromEnd(transitionLine.getStartX(), transitionLine.getStartY(), transitionLine.getEndX(), transitionLine.getEndY(), MainController.circleRadius);
                transitionLine.setStrokeWidth(lineWidth);
                newTransition = new Transition(stateHashMap.get(from), stateHashMap.get(to), serializableTransition.getProbability(), transitionLine);
                newTransition.getCurve().getCubicCurve().setStartX(curvePoints.get(0).getX());
                newTransition.getCurve().getCubicCurve().setStartY(curvePoints.get(0).getY());
                newTransition.getCurve().getCubicCurve().setControlX1(curvePoints.get(1).getX());
                newTransition.getCurve().getCubicCurve().setControlY1(curvePoints.get(1).getY());
                newTransition.getCurve().getCubicCurve().setControlX2(curvePoints.get(2).getX());
                newTransition.getCurve().getCubicCurve().setControlY2(curvePoints.get(2).getY());
                newTransition.getCurve().getCubicCurve().setEndX(curvePoints.get(3).getX());
                newTransition.getCurve().getCubicCurve().setEndY(curvePoints.get(3).getY());
                newTransition.getCurve().getCnt1().setCenterX(dragPoints.get(0).getX());
                newTransition.getCurve().getCnt1().setCenterY(dragPoints.get(0).getY());
                newTransition.getCurve().getCnt2().setCenterX(dragPoints.get(1).getX());
                newTransition.getCurve().getCnt2().setCenterY(dragPoints.get(1).getY());
                if (newTransition.getProbability() != -1)
                    newTransition.getCurve().getProbabilityLabel().setText(Double.toString(serializableTransition.getProbability()));
                newTransition.getCurve().setArrow();
                stateHashMap.get(from).getTransitions().add(newTransition);

                if (from.compareTo(to) != 0)
                    newTransition.getCurve().setArrow();
                else {

                    newTransition.getCurve().setArrowSelf(newTransition.getCurve().getCnt2().getCenterX(), newTransition.getCurve().getCnt2().getCenterY());
                }
            } else {
                LineDistanceManipulator lineDistanceManipulator = new LineDistanceManipulator();
                Line transitionLine = lineDistanceManipulator.getLineAlteredFromBeginning(stateHashMap.get(from).getState().getCenterX(), stateHashMap.get(from).getState().getCenterY(), observationHashMap.get(to).getOrigin().getX(), observationHashMap.get(to).getOrigin().getY(), MainController.circleRadius);
                transitionLine = lineDistanceManipulator.getLineAlteredFromEnd(transitionLine.getStartX(), transitionLine.getStartY(), transitionLine.getEndX(), transitionLine.getEndY(), lineDistanceManipulator.getDistanceToSquareBorder(observationHashMap.get(to).getOrigin().getX(), observationHashMap.get(to).getOrigin().getY(), stateHashMap.get(from).getState().getCenterX(), stateHashMap.get(from).getState().getCenterY()));
                transitionLine.setStrokeWidth(MainController.lineWidth);
                newTransition = new Transition(stateHashMap.get(from), observationHashMap.get(to), serializableTransition.getProbability(), transitionLine);
                newTransition.getObsTo().getStates().add(stateHashMap.get(from));
                newTransition.getCurve().setArrow();
                newTransition.getCurve().getCubicCurve().getStrokeDashArray().addAll(10d, 10d);
                newTransition.getCurve().getCubicCurve().setStartX(curvePoints.get(0).getX());
                newTransition.getCurve().getCubicCurve().setStartY(curvePoints.get(0).getY());
                newTransition.getCurve().getCubicCurve().setControlX1(curvePoints.get(1).getX());
                newTransition.getCurve().getCubicCurve().setControlY1(curvePoints.get(1).getY());
                newTransition.getCurve().getCubicCurve().setControlX2(curvePoints.get(2).getX());
                newTransition.getCurve().getCubicCurve().setControlY2(curvePoints.get(2).getY());
                newTransition.getCurve().getCubicCurve().setEndX(curvePoints.get(3).getX());
                newTransition.getCurve().getCubicCurve().setEndY(curvePoints.get(3).getY());
                newTransition.getCurve().getCnt1().setCenterX(dragPoints.get(0).getX());
                newTransition.getCurve().getCnt1().setCenterY(dragPoints.get(0).getY());
                newTransition.getCurve().getCnt2().setCenterX(dragPoints.get(1).getX());
                newTransition.getCurve().getCnt2().setCenterY(dragPoints.get(1).getY());

                if (newTransition.getProbability() != -1)
                    newTransition.getCurve().getProbabilityLabel().setText(Double.toString(serializableTransition.getProbability()));
                newTransition.getCurve().setArrow();
                stateHashMap.get(from).getTransitions().add(newTransition);


            }
            newTransition.getCurve().setLineMode(serializableTransition.isLineMode());

            mainDrawingPane.getChildren().addAll(newTransition.getCubicCurve(), newTransition.getCurve().getArrow(), newTransition.getCurve().getProbabilityLabel());


        }
        for (State s : currentChain.getStates()) {

            mainDrawingPane.getChildren().add(s.getTestLine());

            mainDrawingPane.getChildren().add(s.getState());

            mainDrawingPane.getChildren().add(s.getStateName());
        }
        for (Observation o : currentChain.getObservations()) {
            mainDrawingPane.getChildren().add(o.getObservation());

            mainDrawingPane.getChildren().add(o.getObservationName());

            ObservationNameHandler observationNameHandler = new ObservationNameHandler();
            observationNameHandler.enableObservationNameOnClick(o);
            enableObservationOnClick(o);


        }


        MakeDraggableState makeDraggableState = new MakeDraggableState(mainDrawingPane.getWidth(), mainDrawingPane.getHeight(), circleRadius);
        for (State s : currentChain.getStates()) {

            makeDraggableState.renewTransitions(s, currentChain, mainDrawingPane, 0.1, 0.1);
            for (Transition t : s.getTransitions()) {
                if (t.getObsTo() != null) {
                    makeDraggableState.renewTransitionsObservation(s, t.getObsTo(), currentChain, mainDrawingPane, 0.1, 0.1);
                }
            }
        }


    }

    public void openProject(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("SER", "SER", "*.ser"));

        try {
            File selectedFile = fileChooser.showOpenDialog(mainScrollPane.getScene().getWindow());
            if (selectedFile.exists() && !selectedFile.isDirectory()) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(selectedFile);
                    openProject(fileInputStream);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }

        } catch (NullPointerException e) {

        }

    }

    public void scrollPaneClick(MouseEvent mouseEvent) {
    }

    public void quitProgram(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void openSimulatorFeatures(ActionEvent actionEvent) {
        HelpHandler helpHandler = new HelpHandler();
        helpHandler.openHelp(2);
    }

    public void openDrawingFeatures(ActionEvent actionEvent) {
        HelpHandler helpHandler = new HelpHandler();
        helpHandler.openHelp(1);
    }

    public void openOtherFeatures(ActionEvent actionEvent) {
        HelpHandler helpHandler = new HelpHandler();
        helpHandler.openHelp(3);
    }

    public void soSmall(ActionEvent actionEvent) throws IOException, ClassNotFoundException {

        circleRadius = 30;
        observationSide = 40;
        selfTransitionSize = 100;
        arrowWidth = 5;
        arrowLength = 10;
        resetSO();
        soSm.setSelected(true);
        redraw();
    }

    public void soMedium(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        circleRadius = 40;
        observationSide = 50;
        selfTransitionSize = 134;
        arrowWidth = 6.666;
        arrowLength = 13.333;
        resetSO();
        soMe.setSelected(true);
        redraw();
    }

    public void soBig(ActionEvent actionEvent) throws IOException, ClassNotFoundException {

        circleRadius = 50;
        observationSide = 60;
        selfTransitionSize = 167;
        arrowWidth = 8.333;
        arrowLength = 16.666;
        resetSO();
        soBi.setSelected(true);
        redraw();
    }

    public void lSmall(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        lineWidth = 2;
        resetLn();
        lSm.setSelected(true);
        redraw();
    }

    public void lMedium(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        lineWidth = 2.5;
        resetLn();
        lMe.setSelected(true);
        redraw();
    }

    public void lBig(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        lineWidth = 3;
        resetLn();
        lBi.setSelected(true);
        redraw();
    }


    enum Option {
        STATE, OBSERVATION, TRANSITION, SELECT, MULTIPLESELECT, NONE
    }

    enum Selection {
        TRUE, FALSE
    }

    public Option getCurrentOption() {
        return currentOption;
    }


    public void setCurrentOption(Option currentOption) {
        this.currentOption = currentOption;


    }


}
