package controllers;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.*;
import jeigen.DenseMatrix;
import sample.*;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class SimulatorController extends MainController {
    @FXML
    public Label stepCounter;

    public Label roundsCounter;
    public Button trainButton;
    public Separator separator;
    @FXML
    private MenuItem stateSequenceItem;
    @FXML
    private MenuItem backwardItem;
    @FXML
    private MenuItem softItem;
    @FXML
    private MenuItem estimationItem;
    @FXML
    private MenuItem decodingItem;
    @FXML
    private MenuItem trainingItem;
    @FXML
    private CheckMenuItem v50;
    @FXML
    private CheckMenuItem v75;
    @FXML
    private CheckMenuItem v100;


    private int trainingRounds = 0;

    @FXML
    ScrollPane simulatorScrollPane;
    @FXML
    Pane simulatorMainDrawingPane;
    @FXML
    ImageView selectionCursorView;

    double viewScale = -1;

    public static int windowCount = 0;
    protected String trainingData = null;

    private boolean hasObservations = false;

    private int stepCount = 0;

    private List<State> simulatorChain;
    protected List<Observation> simulatorObservations;
    private HashMap<State, Label> stateLabelHashMap;
    protected List<StepProbability> stepProbabilities;
    private Mode mode = Mode.NULL;
    private IncidenceMatrixChain incidenceMatrixChain;
    List<double[]> stationaryDistributions;
    List<double[][]> previousDistributions = new ArrayList<>();
    protected HashMap<String[], Double> transitionHashMap;
    protected HashMap<String[], Double> observationHashMap;
    protected HashMap<String, Integer> stateOrder;
    protected TreeMap<Integer, String> stateOrderTree;
    protected Set<String> obsNameSet;
    private HashMap<String, Double> observationHashMapBackup;
    private List<MenuItem> items;


    public List<State> getSimulatorChain() {
        return simulatorChain;
    }


    public State mkState(double x, double y, String stateName, Chain currentChain) {
        State state = new State(constructCircle(x, y, circleRadius), currentChain);
        state.getStateName().setText(stateName);
        state.updateStateNamePosition();
        return state;

    }

    private void resetV() {
        v50.setSelected(false);
        v75.setSelected(false);
        v100.setSelected(false);
    }


    public List<State> makeDeepCopy(List<State> chain) {
        HashMap<String, State> nameMap = new HashMap<>();
        HashMap<String, Observation> obsMap = new HashMap<>();
        List<State> newChain = new ArrayList<>();

        List<Observation> observations = new ArrayList<>();
        stepProbabilities = new ArrayList<>();
        List<Observation> newObservations = new ArrayList<>();
        Chain chainForm = new Chain(newChain, newObservations);
        for (State state : chain) {
            State newState = mkState(state.getState().getCenterX(), state.getState().getCenterY(), state.getStateName().getText(), chainForm);
            newChain.add(newState);
            nameMap.put(newState.getStateName().getText(), newState);
            for (Transition t : state.getTransitions()) {
                if (t.getObsTo() != null) {
                    if (!observations.contains(t.getObsTo())) {
                        observations.add(t.getObsTo());
                        if (hasObservations == false)
                            hasObservations = true;
                    }
                }
            }
        }

        for (Observation o : observations) {
            Observation observation = new Observation(o.getOrigin().getX(), o.getOrigin().getY(), chainForm);
            simulatorMainDrawingPane.getChildren().add(observation.getObservation());
            observation.getObservationName().setText(o.getObservationName().getText());
            obsMap.put(observation.getObservationName().getText(), observation);
            chainForm.getObservations().add(observation);
            simulatorMainDrawingPane.getChildren().add(observation.getObservationName());
        }
        for (Observation obs : chainForm.getObservations()) {
            obs.updateObservationNamePosition();
        }

        for (State s : chain) {
            for (Transition t : s.getTransitions()) {
                State s1 = nameMap.get(t.getFrom().getStateName().getText());
                State s2 = null;
                Observation obs = null;
                if (t.getObsTo() == null)
                    s2 = nameMap.get(t.getTo().getStateName().getText());
                else
                    obs = t.getObsTo();

                LineDistanceManipulator lineDistanceManipulator = new LineDistanceManipulator();
                Line transitionLine;
                Transition newTransition;
                if (t.getObsTo() == null) {
                    transitionLine = lineDistanceManipulator.getLineAlteredFromBeginning(s1.getState().getCenterX(), s1.getState().getCenterY(), s2.getState().getCenterX(), s2.getState().getCenterY(), MainController.circleRadius);
                    transitionLine = lineDistanceManipulator.getLineAlteredFromEnd(transitionLine.getStartX(), transitionLine.getStartY(), transitionLine.getEndX(), transitionLine.getEndY(), MainController.circleRadius);
                    transitionLine.setStrokeWidth(MainController.lineWidth);
                    newTransition = new Transition(s1, s2, t.getProbability(), transitionLine);
                    newTransition.setProbability(t.getProbability());
                    newTransition.getCurve().getProbabilityLabel().setText(t.getCurve().getProbabilityLabel().getText());
                } else {
                    transitionLine = lineDistanceManipulator.getLineAlteredFromBeginning(s1.getState().getCenterX(), s1.getState().getCenterY(), obs.getOrigin().getX(), obs.getOrigin().getY(), MainController.circleRadius);
                    transitionLine = lineDistanceManipulator.getLineAlteredFromEnd(transitionLine.getStartX(), transitionLine.getStartY(), transitionLine.getEndX(), transitionLine.getEndY(), lineDistanceManipulator.getDistanceToSquareBorder(obs.getOrigin().getX(), obs.getOrigin().getY(), transitionLine.getStartX(), transitionLine.getStartY()));
                    transitionLine.setStrokeWidth(MainController.lineWidth);
                    newTransition = new Transition(s1, obsMap.get(obs.getObservationName().getText()), t.getProbability(), transitionLine);
                    newTransition.setProbability(t.getProbability());
                    newTransition.getCurve().getProbabilityLabel().setText(t.getCurve().getProbabilityLabel().getText());
                    newTransition.getCurve().getCubicCurve().getStrokeDashArray().addAll(10d, 10d);
                    obsMap.get(obs.getObservationName().getText()).getStates().add(newTransition.getFrom());
                }
                newTransition.getCubicCurve().setStartX(t.getCubicCurve().getStartX());
                newTransition.getCubicCurve().setStartY(t.getCubicCurve().getStartY());
                newTransition.getCubicCurve().setEndX(t.getCubicCurve().getEndX());
                newTransition.getCubicCurve().setEndY(t.getCubicCurve().getEndY());
                newTransition.getCubicCurve().setControlX1(t.getCubicCurve().getControlX1());
                newTransition.getCubicCurve().setControlY1(t.getCubicCurve().getControlY1());
                newTransition.getCubicCurve().setControlX2(t.getCubicCurve().getControlX2());
                newTransition.getCubicCurve().setControlY2(t.getCubicCurve().getControlY2());
                newTransition.getCurve().setCnt1(new Circle(t.getCurve().getCnt1().getCenterX(), t.getCurve().getCnt1().getCenterY(), 5));
                newTransition.getCurve().setCnt2(new Circle(t.getCurve().getCnt2().getCenterX(), t.getCurve().getCnt2().getCenterY(), 5));

                if (s1 != s2)
                    newTransition.getCurve().setArrow();
                else
                    newTransition.getCurve().setArrowSelf(newTransition.getCubicCurve().getControlX2(), newTransition.getCubicCurve().getControlY2());
                s1.getTransitions().add(newTransition);
            }
        }
        for (State s : newChain) {
            s.updateStateNamePosition();
        }

        return newChain;
    }

    public double[] getStationaryVectorFromRow(DenseMatrix dm) {
        System.out.println(" ");
        double vector[] = new double[dm.rows];
        double sum = 0;
        for (int j = 0; j < dm.rows; j++) {
            vector[j] = dm.get(j, 0);
            sum += dm.get(j, 0);
        }

        for (int i = 0; i < dm.rows; i++) {
            vector[i] /= sum;

        }
        sum = 0;
        for (int i = 0; i < dm.rows; i++) {
            sum += vector[i];

        }


        return vector;

    }

    public void getStationaryDistribution() {
        stationaryDistributions = new ArrayList<>();

        DenseMatrix dm1 = new DenseMatrix(incidenceMatrixChain.getIncidenceMatrix());
        DenseMatrix.PseudoEigenResult res = dm1.t().peig();
        DenseMatrix values = res.values;
        DenseMatrix vectors = res.vectors;
        for (int i = 0; i < values.cols; i++) {
            for (int j = 0; j < values.cols; j++) {
                if (((values.get(i, j) > 0.99) && (values.get(i, j) < 1.0000000000000010))) {

                    DenseMatrix row = vectors.col(j);
                    double[] st = getStationaryVectorFromRow(row);
                    if (st != null)
                        stationaryDistributions.add(st);
                }
            }
        }

    }

    public void initQueryMode() {
        transitionHashMap = new HashMap<>();
        observationHashMap = new HashMap<>();
        stateOrder = incidenceMatrixChain.getStateOrder();
        stateOrderTree = incidenceMatrixChain.getStateOrderTree();
        obsNameSet = new HashSet<>();
        observationHashMapBackup = new HashMap<>();


        for (State s : simulatorChain) {
            for (Transition t : s.getTransitions()) {
                if (t.getObsTo() == null) {
                    transitionHashMap.put(new String[]{t.getFrom().getStateName().getText(), t.getTo().getStateName().getText()}, t.getProbability());
                } else {
                    observationHashMap.put(new String[]{t.getObsTo().getObservationName().getText(), t.getFrom().getStateName().getText()}, t.getProbability());
                    observationHashMapBackup.put(t.getObsTo().getObservationName().getText() + "#" + t.getFrom().getStateName().getText(), t.getProbability());
                    obsNameSet.add(t.getObsTo().getObservationName().getText());
                }
            }
        }


    }


    public void viterbiQuery(String queryInputObs) {
        String[] query = queryInputObs.split(",");
        for (String st : query) {
            if (obsNameSet.contains(st) == false) {

                return;

            }
        }
        double sum = 0;
        double[] currentStep = new double[simulatorChain.size()];
        int i = 0;
        for (StepProbability s : stepProbabilities) {
            sum += s.getStepProbability();
            currentStep[i] = s.getStepProbability();
            i += 1;
        }


        //double[][] probabilityMatrix=new double[simulatorChain.size()][query.length];
        BigDecimal[][] probabilityMatrix = new BigDecimal[simulatorChain.size()][query.length];


        for (i = 0; i < simulatorChain.size(); i++) {
            String[] str = {query[0], stateOrderTree.get(i)};

            for (String[] key : observationHashMap.keySet()) {
                if ((key[0].contains(query[0])) && (key[1].contains(stateOrderTree.get(i))))
                    probabilityMatrix[i][0] = BigDecimal.valueOf(currentStep[i]).multiply(BigDecimal.valueOf(observationHashMap.get(key)));
            }

        }

        for (i = 1; i < query.length; i++) {
            for (int j = 0; j < simulatorChain.size(); j++) {
                BigDecimal max = BigDecimal.valueOf(-1);

                for (int x = 0; x < simulatorChain.size(); x++) {
                    double thm = 0, ohm = 0;
                    boolean contains = false;
                    for (String[] key : transitionHashMap.keySet()) {
                        if ((key[0].contains(stateOrderTree.get(x))) && (key[1].contains(stateOrderTree.get(j)))) {
                            thm = transitionHashMap.get(key);
                            contains = true;

                        }
                    }
                    for (String[] key : observationHashMap.keySet()) {
                        if ((key[0].contains(query[i])) && (key[1].contains(stateOrderTree.get(j)))) {
                            ohm = observationHashMap.get(key);

                        }
                    }

                    if (contains == true) {
                        try {
                            if (probabilityMatrix[x][i - 1].multiply(BigDecimal.valueOf(thm)).multiply(BigDecimal.valueOf(ohm)).compareTo(max) == 1) {
                                max = probabilityMatrix[x][i - 1].multiply(BigDecimal.valueOf(thm)).multiply(BigDecimal.valueOf(ohm));
                            }
                        } catch (NullPointerException e) {


                        }
                    }

                }

                probabilityMatrix[j][i] = max;

            }

        }

        List<String> result = new ArrayList<>();


        for (i = 0; i < query.length; i++) {
            BigDecimal max = BigDecimal.ZERO;
            int x = 0;
            for (int j = 0; j < simulatorChain.size(); j++) {

                if (probabilityMatrix[j][i].compareTo(max) == 1) {
                    max = probabilityMatrix[j][i];
                    x = j;
                }

            }
            result.add(stateOrderTree.get(x));
        }


        displayDecodingQuery(probabilityMatrix, query);

    }


    public void changePositionOfChain(List<State> chain, double Dx, double Dy) {

        List<Observation> observations = new ArrayList<>();
        for (State s : chain) {
            s.getState().setCenterX(s.getState().getCenterX() + Dx);
            s.getState().setCenterY(s.getState().getCenterY() + Dy);
            s.getTestLine().setStartY(s.getTestLine().getStartY() + Dy);
            s.getTestLine().setEndY(s.getTestLine().getEndY() + Dy);
            s.getTestLine().setStartX(s.getTestLine().getStartX() + Dx);
            s.getTestLine().setEndX(s.getTestLine().getEndX() + Dx);

            s.getStateName().setLayoutY(s.getStateName().getLayoutY() + Dy);
            s.getStateName().setLayoutX(s.getStateName().getLayoutX() + Dx);


            // s.updateStateNamePosition();
            for (Transition transition : s.getTransitions()) {
                if (transition.getObsTo() != null) {
                    if (!observations.contains(transition.getObsTo()))
                        observations.add(transition.getObsTo());
                }
                transition.getCubicCurve().setStartX(transition.getCubicCurve().getStartX() + Dx);
                transition.getCubicCurve().setStartY(transition.getCubicCurve().getStartY() + Dy);
                transition.getCubicCurve().setControlX1(transition.getCubicCurve().getControlX1() + Dx);
                transition.getCubicCurve().setControlY1(transition.getCubicCurve().getControlY1() + Dy);
                transition.getCubicCurve().setControlX2(transition.getCubicCurve().getControlX2() + Dx);
                transition.getCubicCurve().setControlY2(transition.getCubicCurve().getControlY2() + Dy);
                transition.getCubicCurve().setEndX(transition.getCubicCurve().getEndX() + Dx);
                transition.getCubicCurve().setEndY(transition.getCubicCurve().getEndY() + Dy);
                transition.getCurve().getCnt1().setCenterX(transition.getCurve().getCnt1().getCenterX() + Dx);
                transition.getCurve().getCnt1().setCenterY(transition.getCurve().getCnt1().getCenterY() + Dy);
                transition.getCurve().getCnt2().setCenterX(transition.getCurve().getCnt2().getCenterX() + Dx);
                transition.getCurve().getCnt2().setCenterY(transition.getCurve().getCnt2().getCenterY() + Dy);

                if (transition.getFrom() != transition.getTo())
                    transition.getCurve().setArrow();
                else {
                    transition.getCurve().setArrowSelf(transition.getCubicCurve().getControlX2(), transition.getCubicCurve().getControlY2());
                }

            }

        }
        for (Observation observation : observations) {
            observation.getObservation().setX(observation.getObservation().getX() + Dx);
            observation.getObservation().setY(observation.getObservation().getY() + Dy);
            observation.getOrigin().setX(observation.getObservation().getX() + Dx);
            observation.getOrigin().setY(observation.getObservation().getY() + Dy);
            observation.getObservationName().setLayoutX(observation.getObservationName().getLayoutX() + Dx);
            observation.getObservationName().setLayoutY(observation.getObservationName().getLayoutY() + Dy);
        }

    }

    public void setProbabilityLabelListeners(StepProbability stepProbability, List<StepProbability> stepProbabilities, List<MenuItem> items) {

        stepProbability.getLabel().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (mode == Mode.SELECTION) {
                    if (windowCount == 0)
                        try {
                            windowCount += 1;
                            Stage stage = new Stage();
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/setStepProbability.fxml"));
                            Parent root = fxmlLoader.load();
                            stage.initStyle(StageStyle.UNDECORATED);
                            stage.initModality(Modality.WINDOW_MODAL);
                            stage.setResizable(false);
                            stage.setTitle("Set Probability");
                            Bounds boundsInScreen = stepProbability.getLabel().localToScreen(stepProbability.getLabel().getBoundsInLocal());
                            stage.setX(boundsInScreen.getMaxX());
                            stage.setY(boundsInScreen.getMaxY());
                            stage.setScene(new Scene(root));
                            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                                @Override
                                public void handle(WindowEvent event) {
                                    windowCount -= 1;
                                }
                            });

                            SetStepProbabilityController setStepProbabilityController = fxmlLoader.getController();
                            setStepProbabilityController.init(stepProbabilities, stepProbability, items, hasObservations);

                            stage.show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }


            }
        });
    }

    public void setProbabilityLabels(Chain chain) {
        for (State s : chain.getStates()) {
            Label label = new Label("NaN");
            label.setTextFill(Paint.valueOf("8b0000"));
            simulatorMainDrawingPane.getChildren().add(label);
            StepProbability stepProbability = new StepProbability(label, s);
            stepProbabilities.add(stepProbability);
            updateStateNamePosition(label, s);
            setProbabilityLabelListeners(stepProbability, stepProbabilities, items);
        }
    }

    public void updateStateNamePosition(Label label, State state) {
        Text theText = new Text(label.getText());
        theText.setFont(label.getFont());
        double width = theText.getBoundsInLocal().getWidth();

        LineDistanceManipulator lineDistanceManipulator = new LineDistanceManipulator();
        Line ln = lineDistanceManipulator.getLineAlteredFromBeginning(state.getState().getCenterX() - MainController.circleRadius / 2, state.getState().getCenterY(), state.getState().getCenterX() + MainController.circleRadius / 2, state.getState().getCenterY(), (MainController.circleRadius - width) / 2);

        label.setLayoutX(ln.getStartX());
        label.setLayoutY(state.getState().getCenterY() - (circleRadius / 1.4));

    }

    public void enableDragOnState(State state, List<State> states) {
        state.getTestLine().setStrokeWidth(0);
        state.getStateName().setOnMouseClicked(null);
        state.getState().setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if (mode == Mode.SELECTION) {
                    double Dx = event.getX() - state.getState().getCenterX();
                    double Dy = event.getY() - state.getState().getCenterY();
                    changePositionOfChain(states, Dx, Dy);
                    for (StepProbability s : stepProbabilities) {
                        updateStateNamePosition(s.getLabel(), s.getState());
                    }
                }
            }
        });


        state.getState().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {


            }
        });
    }

    public void setSimulatorChain(List<State> chain) {
        decodingItem.setDisable(true);
        estimationItem.setDisable(true);
        trainingItem.setDisable(true);
        softItem.setDisable(true);
        backwardItem.setDisable(true);
        stateSequenceItem.setDisable(true);

        items = new ArrayList<>();
        items.add(decodingItem);
        items.add(estimationItem);
        items.add(trainingItem);
        items.add(softItem);
        items.add(backwardItem);
        items.add(stateSequenceItem);

        roundsCounter.setVisible(false);
        trainButton.setVisible(false);
        separator.setVisible(false);

        this.simulatorChain = makeDeepCopy(chain);
        stateLabelHashMap = new HashMap<>();
        int n = incidenceMatrixChain.getStateCount();


        for (State s : simulatorChain) {


            for (Transition transition : s.getTransitions()) {
                simulatorMainDrawingPane.getChildren().add(transition.getCubicCurve());
                simulatorMainDrawingPane.getChildren().add(transition.getCurve().getArrow());
                simulatorMainDrawingPane.getChildren().add(transition.getCurve().getProbabilityLabel());
            }
            simulatorMainDrawingPane.getChildren().addAll(s.getState(), s.getTestLine(), s.getStateName());

        }
        double minx = Integer.MAX_VALUE, miny = Integer.MAX_VALUE;

        simulatorObservations = new ArrayList<>();
        for (State s : simulatorChain) {
            enableDragOnState(s, simulatorChain);

            if (s.getState().getCenterX() < minx)
                minx = s.getState().getCenterX();

            if (s.getState().getCenterY() < miny)
                miny = s.getState().getCenterY();


            for (Transition t : s.getTransitions()) {
                if (t.getObsTo() != null) {
                    if (!simulatorObservations.contains(t.getObsTo())) {
                        simulatorObservations.add(t.getObsTo());
                    }
                }
                if (t.getCurve().getCnt1().getCenterX() < minx) {
                    minx = t.getCurve().getCnt1().getCenterX();
                }

                if (t.getCurve().getCnt2().getCenterX() < minx) {
                    minx = t.getCurve().getCnt2().getCenterX();
                }

                if (t.getCurve().getCnt1().getCenterY() < miny) {
                    miny = t.getCurve().getCnt1().getCenterY();
                }

                if (t.getCurve().getCnt2().getCenterY() < miny) {
                    miny = t.getCurve().getCnt2().getCenterY();
                }


            }

        }
        for (Observation observation : simulatorObservations) {
            if (observation.getObservation().getX() < minx)
                minx = observation.getObservation().getX();
            if (observation.getObservation().getY() < miny) {
                miny = observation.getObservation().getY();
            }
        }
        double Dx = 0, Dy = 0;
        Dx = minx - 50;
        Dy = miny - 50;
        Chain chain1 = new Chain(simulatorChain, simulatorObservations);
        changePositionOfChain(simulatorChain, -Dx, -Dy);
        setProbabilityLabels(chain1);
        getStationaryDistribution();
        initQueryMode();
    }


    public void colorSelection() {
        if (mode == Mode.SELECTION) {
            selectionCursorView.setImage(new Image("images/redCursor.png"));
        } else if (mode == Mode.NULL) {
            selectionCursorView.setImage(new Image("images/cursor.png"));
        }
    }

    public void enableSimulatorSelectionMode(MouseEvent mouseEvent) {

        if (mode == Mode.NULL) {
            mode = Mode.SELECTION;
            colorSelection();
        } else {
            mode = Mode.NULL;
            colorSelection();
        }
    }

    public BigDecimal[][] backwardQuery(String queryInputObs, boolean showOff) {
        String[] query = queryInputObs.split(",");
        for (String st : query) {
            if (obsNameSet.contains(st) == false) {

                return null;

            }
        }
        double sum = 0;
        double[] currentStep = new double[simulatorChain.size()];
        int i = 0;
        for (StepProbability s : stepProbabilities) {
            sum += s.getStepProbability();
            currentStep[i] = s.getStepProbability();
            i += 1;
        }


        //double[][] probabilityMatrix=new double[simulatorChain.size()][query.length];
        BigDecimal[][] probabilityMatrix = new BigDecimal[simulatorChain.size()][query.length];


        for (i = 0; i < simulatorChain.size(); i++) {
            probabilityMatrix[i][query.length - 1] = BigDecimal.ONE;
        }

        for (i = query.length - 2; i >= 0; i--) {
            for (int j = 0; j < simulatorChain.size(); j++) {
                BigDecimal max = BigDecimal.valueOf(0);

                for (int x = 0; x < simulatorChain.size(); x++) {
                    double thm = 0, ohm = 0;
                    boolean contains = false;
                    for (String[] key : transitionHashMap.keySet()) {
                        if ((key[0].contains(stateOrderTree.get(j))) && (key[1].contains(stateOrderTree.get(x)))) {
                            thm = transitionHashMap.get(key);
                            contains = true;

                        }
                    }
                    for (String[] key : observationHashMap.keySet()) {
                        if ((key[0].contains(query[i + 1])) && (key[1].contains(stateOrderTree.get(x)))) {
                            ohm = observationHashMap.get(key);

                        }
                    }

                    if (contains == true) {
                        try {
                            max = max.add(probabilityMatrix[x][i + 1].multiply(BigDecimal.valueOf(thm)).multiply(BigDecimal.valueOf(ohm)));
                        } catch (NullPointerException e) {

                        }
                    }

                }

                probabilityMatrix[j][i] = max;

            }

        }


        BigDecimal result = BigDecimal.ZERO;
        for (i = 0; i < simulatorChain.size(); i++) {
            result = result.add(probabilityMatrix[i][query.length - 1]);

        }

        if (showOff == true) {
            displayBackward(probabilityMatrix, query);
        }

        return probabilityMatrix;
    }

    public void trainOneRound(String queryInputObs) {
        BigDecimal[][] forward = estimationQuery(queryInputObs, false);
        BigDecimal[][] backward = backwardQuery(queryInputObs, false);
        String[] query = queryInputObs.split(",");
        BigDecimal[][] fwb = new BigDecimal[simulatorChain.size()][query.length];
        BigDecimal norm = BigDecimal.ZERO;
        for (int i = 0; i < simulatorChain.size(); i++) {
            norm = norm.add(forward[i][query.length - 1]);
        }
        for (int i = 0; i < simulatorChain.size(); i++) {
            for (int j = 0; j < query.length; j++) {

                fwb[i][j] = forward[i][j].multiply(backward[i][j]).divide(norm, 200, RoundingMode.HALF_UP);

            }
        }

        ///UPDATE TRANSITIONS
        HashMap<String, Double> newTransitionHashMap = new HashMap<>();
        for (String[] key : transitionHashMap.keySet()) {
            newTransitionHashMap.put(key[0].concat("#").concat(key[1]), transitionHashMap.get(key));
        }
        HashMap<String, Double> newObservationHashMap = new HashMap<>();
        for (String[] key : observationHashMap.keySet()) {
            newObservationHashMap.put(key[0].concat("#").concat(key[1]), observationHashMap.get(key));
        }

        HashMap<Integer, HashMap<String, BigDecimal>> zeta = new HashMap<>();


        for (int t = 0; t < query.length - 1; t++) {
            HashMap<String, BigDecimal> zetaHash=new HashMap<>();
            zetaHash=zetaStep(t, newTransitionHashMap, newObservationHashMap, backward, forward, query);
            zeta.put(t, zetaHash);

        }


        for (String key : newTransitionHashMap.keySet()) {
            String[] kkey = key.split("#");
            BigDecimal denumerator = BigDecimal.ZERO;
            BigDecimal denominator = BigDecimal.ZERO;

            for (int t = 0; t < query.length - 1; t++) {
                denumerator = denumerator.add(zeta.get(t).get(key));
                denominator = denominator.add(fwb[stateOrder.get(kkey[0])][t]);
            }


            for (String[] k : transitionHashMap.keySet()) {
                if ((k[0].contains(kkey[0])) && (k[1].contains(kkey[1]))) {
                    BigDecimal division;

                    division = denumerator.divide(denominator, 200, RoundingMode.HALF_UP);

                    transitionHashMap.put(k, Double.valueOf(division.toString()));
                }
            }

        }

        //UPDATE OBSERVATIONS

        for (String k : newObservationHashMap.keySet()) {
            BigDecimal denominator = BigDecimal.ZERO;
            BigDecimal denumerator = BigDecimal.ZERO;

            int i1 = stateOrder.get(k.split("#")[1]);
            String obs = k.split("#")[0];
            for (int i = 0; i < simulatorChain.size(); i++) {
                for (int j = 0; j < query.length; j++) {
                    if (i1 == i) {
                        denominator = denominator.add(fwb[i][j]);
                        if (query[j].contains(obs)) {
                            denumerator = denumerator.add(fwb[i][j]);
                        }
                    }

                }

            }
            for (String[] st : observationHashMap.keySet()) {
                if ((st[0].contains(k.split("#")[0])) && (st[1].contains(k.split("#")[1]))) {
                    BigDecimal division;
                    division = denumerator.divide(denominator, 200, RoundingMode.HALF_UP);
                    observationHashMap.put(st, Double.valueOf(division.toString()));

                }

            }

            //UPDATE START DISTRIBUTION
            double[] newStateProb = new double[stepProbabilities.size()];
            for (int i = 0; i < simulatorChain.size(); i++) {
                String str = fwb[i][0].toString();

                newStateProb[i] = Double.valueOf(str);

            }
            int i = 0;
            for (StepProbability s : stepProbabilities) {
                s.setStepProbability(newStateProb[i]);
                i += 1;
                String str = Double.toString(s.getStepProbability());
                if (str.contains("E")) {
                    str = "0";
                }

                s.getLabel().setText(str);
                updateStateNamePosition(s.getLabel(), s.getState());

            }

            //UPDATE DISPLAY

            for (State s : simulatorChain) {
                for (Transition t : s.getTransitions()) {
                    String s1 = t.getFrom().getStateName().getText();
                    String s2;
                    String o1;
                    if (t.getObsTo() == null) {
                        s2 = t.getTo().getStateName().getText();
                        for (String[] key : transitionHashMap.keySet()) {
                            if ((key[0].contains(s1)) && (key[1].contains(s2))) {
                                t.setProbability(transitionHashMap.get(key));
                                String str = Double.toString(t.getProbability());
                                if (str.contains("E"))
                                    str = "0";
                                t.getCurve().getProbabilityLabel().setText(str);
                                if (s1.compareTo(s2) == 0)
                                    t.getCurve().setArrowSelf(t.getCubicCurve().getControlX2(), t.getCubicCurve().getControlY2());
                                else {
                                    t.getCurve().setArrow();
                                }
                            }
                        }
                    } else {
                        o1 = t.getObsTo().getObservationName().getText();
                        for (String[] key : observationHashMap.keySet()) {
                            if ((key[0].contains(o1)) && (key[1].contains(s1))) {
                                t.setProbability(observationHashMap.get(key));
                                String str = Double.toString(t.getProbability());
                                if (str.contains("E"))
                                    str = "0";
                                t.getCurve().getProbabilityLabel().setText(str);
                                t.getCurve().setArrow();

                            }
                        }
                    }

                }
            }


        }


    }

    public HashMap<String, BigDecimal> zetaStep(int t, HashMap<String, Double> newTransitionHashMap, HashMap<String, Double> newObservationHashMap, BigDecimal[][] backward, BigDecimal[][] forward, String[] query) {

        BigDecimal sum = BigDecimal.ZERO;
        for (String key : newTransitionHashMap.keySet()) {
            String[] kkey = key.split("#");
            sum = sum.add(forward[stateOrder.get(kkey[0])][t].multiply(backward[stateOrder.get(kkey[1])][t + 1]).multiply(BigDecimal.valueOf(newTransitionHashMap.get(key))).multiply(BigDecimal.valueOf(newObservationHashMap.get(query[t + 1] + "#" + kkey[1]))));
        }

        HashMap<String, BigDecimal> zt = new HashMap<>();
        for (String key : newTransitionHashMap.keySet()) {
            String[] kkey = key.split("#");
            BigDecimal division;

            division = forward[stateOrder.get(kkey[0])][t].multiply(backward[stateOrder.get(kkey[1])][t + 1]).multiply(BigDecimal.valueOf(newTransitionHashMap.get(key))).multiply(BigDecimal.valueOf(newObservationHashMap.get(query[t + 1] + "#" + kkey[1]))).divide(sum, 200, RoundingMode.HALF_UP);


            zt.put(key, division);
        }
        return zt;

    }

    public BigDecimal[][] estimationQuery(String queryInputObs, boolean showOff) {
        String[] query = queryInputObs.split(",");
        for (String st : query) {
            if (obsNameSet.contains(st) == false) {

                return null;
            }
        }
        double sum = 0;
        double[] currentStep = new double[simulatorChain.size()];
        int i = 0;
        for (StepProbability s : stepProbabilities) {
            sum += s.getStepProbability();
            currentStep[i] = s.getStepProbability();
            i += 1;
        }


        BigDecimal[][] probabilityMatrix = new BigDecimal[simulatorChain.size()][query.length];

        for (i = 0; i < simulatorChain.size(); i++) {
            String[] str = {query[0], stateOrderTree.get(i)};

            for (String[] key : observationHashMap.keySet()) {
                if ((key[0].contains(query[0])) && (key[1].contains(stateOrderTree.get(i))))
                    probabilityMatrix[i][0] = BigDecimal.valueOf(currentStep[i]).multiply(BigDecimal.valueOf(observationHashMap.get(key)));
            }

        }
        for (i = 1; i < query.length; i++) {
            for (int j = 0; j < simulatorChain.size(); j++) {
                BigDecimal max = BigDecimal.valueOf(0);

                for (int x = 0; x < simulatorChain.size(); x++) {
                    double thm = 0, ohm = 0;
                    boolean contains = false;
                    for (String[] key : transitionHashMap.keySet()) {
                        if ((key[0].contains(stateOrderTree.get(x))) && (key[1].contains(stateOrderTree.get(j)))) {
                            thm = transitionHashMap.get(key);
                            contains = true;

                        }
                    }
                    for (String[] key : observationHashMap.keySet()) {
                        if ((key[0].contains(query[i])) && (key[1].contains(stateOrderTree.get(j)))) {
                            ohm = observationHashMap.get(key);

                        }
                    }

                    if (contains == true) {
                        try {
                            max = max.add(probabilityMatrix[x][i - 1].multiply(BigDecimal.valueOf(thm)).multiply(BigDecimal.valueOf(ohm)));
                        } catch (NullPointerException e) {

                        }
                    }

                }

                probabilityMatrix[j][i] = max;

            }


        }


        BigDecimal result = BigDecimal.ZERO;
        for (i = 0; i < simulatorChain.size(); i++) {
            result = result.add(probabilityMatrix[i][query.length - 1]);

        }


        if (showOff == true) {

            displayEstimationQuery(probabilityMatrix, query);
        }

        return probabilityMatrix;

    }

    public void displayEstimationQuery(BigDecimal[][] probabilityMatrix, String[] query) {
        TableViewHandler tableViewHandler = new TableViewHandler();
        tableViewHandler.displayTable(probabilityMatrix, query, 0, this);

    }

    public void displayBackward(BigDecimal[][] probabilityMatrix, String[] query) {
        TableViewHandler tableViewHandler = new TableViewHandler();
        tableViewHandler.displayTable(probabilityMatrix, query, 3, this);
    }
    public void displayStateSequenceQuery(String[] query)
    {

        TableViewHandler tableViewHandler = new TableViewHandler();
        tableViewHandler.displayTable(query,this);
    }

    public void displayForwardBackward(BigDecimal[][] probabilityMatrix, String[] query) {


        for (int i = 0; i < simulatorChain.size(); i++) {
            for (int j = 0; j < query.length; j++) {
                if (probabilityMatrix[i][j].toString().length() > 11) {
                    if(probabilityMatrix[i][j].toString().contains("E")==true)
                        probabilityMatrix[i][j]=BigDecimal.valueOf(0);
                    else
                    probabilityMatrix[i][j] = BigDecimal.valueOf(Double.valueOf(probabilityMatrix[i][j].toString().substring(0, 10)));
                }
            }
        }


        TableViewHandler tableViewHandler = new TableViewHandler();
        tableViewHandler.displayTable(probabilityMatrix, query, 2, this);
    }

    public void displayDecodingQuery(BigDecimal[][] probabilityMatrix, String[] query) {
        TableViewHandler tableViewHandler = new TableViewHandler();
        tableViewHandler.displayTable(probabilityMatrix, query, 1, this);
    }

    public BigDecimal[][] forwardBackward(String queryInputObs, boolean showOff) {

        BigDecimal[][] forward = estimationQuery(queryInputObs, false);
        BigDecimal[][] backward = backwardQuery(queryInputObs, false);
        String[] query = queryInputObs.split(",");
        BigDecimal[][] fwb = new BigDecimal[simulatorChain.size()][query.length];
        BigDecimal norm = BigDecimal.ZERO;
        for (int i = 0; i < simulatorChain.size(); i++) {
            norm = norm.add(forward[i][query.length - 1]);
        }
        for (int i = 0; i < simulatorChain.size(); i++) {
            for (int j = 0; j < query.length; j++) {
                fwb[i][j] = forward[i][j].multiply(backward[i][j]).divide(norm, 200, RoundingMode.HALF_UP);

            }

        }
        if (showOff) {
            displayForwardBackward(fwb, query);
        }

        return fwb;
    }




    public void stepForward() {
        double[][] currentStep = new double[1][stepProbabilities.size()];

        double sum = 0;
        int i = 0;

        for (StepProbability s : stepProbabilities) {
            sum += s.getStepProbability();
            currentStep[0][i] = s.getStepProbability();
            i += 1;
        }


        if (((sum > 0.99) && (sum < 1.0000000000000010)) || (stepCount > 0)) {
            if (previousDistributions.size() <= stepCount) {
                previousDistributions.add(currentStep);
            }

            DenseMatrix d1 = new DenseMatrix(currentStep);
            DenseMatrix d2 = new DenseMatrix(incidenceMatrixChain.getIncidenceMatrix());
            d2 = d1.mmul(d2);
            i = 0;
            for (StepProbability s : stepProbabilities) {
                s.setStepProbability(d2.get(0, i));
                String str = Double.toString(d2.get(0, i));
                if (str.length() > 5) {
                    str = str.substring(0, 6);
                }
                s.getLabel().setText(str);

                updateStateNamePosition(s.getLabel(), s.getState());
                i += 1;

            }
            incrementStepCount();
        }
    }

    public void actualizeLabels(double[][] step) {
        int i = 0;
        for (StepProbability s : stepProbabilities) {
            s.setStepProbability(step[0][i]);
            String str = Double.toString(step[0][i]);
            if (str.length() > 5) {
                str = str.substring(0, 6);
            }
            s.getLabel().setText(str);
            updateStateNamePosition(s.getLabel(), s.getState());
            i += 1;
        }
    }

    public void incrementStepCount() {
        stepCount += 1;
        stepCounter.setText(Integer.toString(stepCount));
    }

    public void decrementStepCount() {
        if (stepCount > 0) {
            actualizeLabels(previousDistributions.get(stepCount - 1));
            stepCount -= 1;
            stepCounter.setText(Integer.toString(stepCount));

        }
    }

    public IncidenceMatrixChain getIncidenceMatrixChain() {
        return incidenceMatrixChain;
    }

    public void setIncidenceMatrixChain(IncidenceMatrixChain incidenceMatrixChain) {
        this.incidenceMatrixChain = incidenceMatrixChain;
    }

    public void stepForwardMultipleTimes(ActionEvent actionEvent) {
        for (int i = 0; i < 5; i++) {
            stepForward();
        }
    }

    public void stepBackwardMultipleTimes(ActionEvent actionEvent) {
        for (int i = 0; i < 5; i++) {
            decrementStepCount();
        }
    }

    public void initViterbyQuery(ActionEvent actionEvent) {
        ViterbyQueryHandler viterbyQueryHandler = new ViterbyQueryHandler();
        viterbyQueryHandler.openViterbiQuery(this);
    }
    public void initStateSequenceProbability(ActionEvent actionEvent) {

        StateSequenceProbabilityHandler stateSequenceProbabilityHandler=new StateSequenceProbabilityHandler();
        stateSequenceProbabilityHandler.displayStateSequenceProbability(this);
    }

    public void initBackward(ActionEvent actionEvent) {
        BackwardHandler backwardHandler = new BackwardHandler();
        backwardHandler.openBackwardQuery(this);
    }

    public void initForwardBackward(ActionEvent actionEvent) {
        SoftClusteringHandler softClusteringHandler = new SoftClusteringHandler();
        softClusteringHandler.openSoftClusteringQuery(this);
    }


    public void initEstimation(ActionEvent actionEvent) {
        EstimationQueryHandler estimationQueryHandler = new EstimationQueryHandler();
        estimationQueryHandler.openEstimationQuery(this);
    }

    public void initTraining(ActionEvent actionEvent) {
        TrainingHandler trainingHandler = new TrainingHandler();
        trainingHandler.openTraining(this);

    }

    public void resetModel(ActionEvent actionEvent) {
        for (State s : simulatorChain) {
            for (Transition t : s.getTransitions()) {
                if (t.getObsTo() == null) {
                    int i = stateOrder.get(t.getFrom().getStateName().getText());
                    int j = stateOrder.get(t.getTo().getStateName().getText());
                    t.setProbability(incidenceMatrixChain.getIncidenceMatrix()[i][j]);

                    t.getCurve().getProbabilityLabel().setText(Double.toString(t.getProbability()));
                    if (t.getFrom() != t.getTo()) {
                        t.getCurve().setArrow();
                    } else {
                        t.getCurve().setArrowSelf(t.getCubicCurve().getControlX2(), t.getCubicCurve().getControlY2());
                    }
                } else {

                    t.setProbability(observationHashMapBackup.get(t.getObsTo().getObservationName().getText() + "#" + t.getFrom().getStateName().getText()));
                    for (String[] key : observationHashMap.keySet()) {
                        if ((key[0].contains(t.getObsTo().getObservationName().getText())) && (key[1].contains(t.getFrom().getStateName().getText()))) {
                            observationHashMap.put(key, t.getProbability());
                        }
                    }
                    t.getCurve().getProbabilityLabel().setText(Double.toString(t.getProbability()));

                    t.getCurve().setArrow();
                }
            }
        }

        for (String[] key : transitionHashMap.keySet()) {
            int i = stateOrder.get(key[0]);
            int j = stateOrder.get(key[1]);
            transitionHashMap.put(key, incidenceMatrixChain.getIncidenceMatrix()[i][j]);
        }
        for (StepProbability stepProbability : stepProbabilities) {
            stepProbability.getLabel().setText("NaN");
            stepProbability.getLabel().setTextFill(Paint.valueOf("8b0000"));
            stepProbability.setStepProbability(-1);
            updateStateNamePosition(stepProbability.getLabel(), stepProbability.getState());
        }
        previousDistributions = new ArrayList<>();
        stepCount = 0;
        stepCounter.setText(Integer.toString(stepCount));

        trainingRounds = 0;
        roundsCounter.setText("Rounds:"+trainingRounds +" Training Data:"+trainingData);
        trainingData = null;
        separator.setVisible(false);
        trainButton.setVisible(false);
        roundsCounter.setVisible(false);
        for (MenuItem menuItem : items) {
            menuItem.setDisable(true);
        }


    }

    public void startTraining(ActionEvent actionEvent) {

        for (StepProbability stepProbability : stepProbabilities) {
            if (stepProbability.getLabel().getTextFill().toString().contains("8b0000")) {
                return;
            }
        }

        trainOneRound(trainingData);
        trainingRounds += 1;
        roundsCounter.setText("Rounds:" + trainingRounds + " , Training Data:" + trainingData);


    }

    public void showStationaryDistribution(ActionEvent actionEvent) {

        StationaryDistributionHandler stationaryDistributionHandler = new StationaryDistributionHandler();
        stationaryDistributionHandler.displayStationaryDistribution(this, stationaryDistributions);
    }

    public void getModelData(ActionEvent actionEvent) {
        ModelDataHandler modelDataHandler = new ModelDataHandler();
        modelDataHandler.openModelHandler(this);
    }

    public void takeSnapshot(ActionEvent actionEvent) throws IOException {

        simulatorScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        simulatorScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        WritableImage writableImage = simulatorMainDrawingPane.snapshot(new SnapshotParameters(), null);
        simulatorScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        simulatorScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("snapshot.png");
        fileChooser.setTitle("Save");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "PNG", "*.png"));
        try {
            File selectedFile = fileChooser.showSaveDialog(simulatorScrollPane.getScene().getWindow());
            if (selectedFile.exists() && !selectedFile.isDirectory()) {
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", selectedFile);

            } else {
                File f = new File(selectedFile.getAbsolutePath());
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", f);
            }

        } catch (NullPointerException e) {

        }


    }

    public void changeScale() {
        Scale scale = new Scale();
        scale.setY(viewScale);
        scale.setX(viewScale);
        scale.setPivotY(0);
        scale.setPivotX(0);
        simulatorMainDrawingPane.getTransforms().removeAll(simulatorMainDrawingPane.getTransforms());
        simulatorMainDrawingPane.getTransforms().add(scale);
    }

    public void view50(ActionEvent actionEvent) {

        viewScale = 0.5;
        changeScale();
        resetV();
        v50.setSelected(true);

    }

    public void view75(ActionEvent actionEvent) {
        viewScale = 0.75;
        changeScale();
        resetV();
        v75.setSelected(true);

    }

    public void view100(ActionEvent actionEvent) {

        viewScale = 1;
        changeScale();
        resetV();
        v100.setSelected(true);
    }

    public void quitSimulator(ActionEvent actionEvent) {
        Stage st = (Stage) simulatorMainDrawingPane.getScene().getWindow();
        st.close();
    }

    public void shortenNumbers(ActionEvent actionEvent) {
        for (StepProbability s : stepProbabilities) {
            String str = s.getLabel().getText();
            if (str.contains("E")) {
                str = "0";
            }
            if (str.length() > 8)
                str = str.substring(0, 7);

            s.getLabel().setText(str);
            updateStateNamePosition(s.getLabel(), s.getState());
        }
        for (State s : simulatorChain) {

            for (Transition t : s.getTransitions()) {
                String s1 = t.getFrom().getStateName().getText();
                String s2;
                String o1;
                if (t.getObsTo() == null) {
                    s2 = t.getTo().getStateName().getText();
                    for (String[] key : transitionHashMap.keySet()) {
                        if ((key[0].contains(s1)) && (key[1].contains(s2))) {
                            t.setProbability(transitionHashMap.get(key));
                            String str = Double.toString(t.getProbability());
                            if (str.contains("E"))
                                str = "0";
                            if (str.length() > 8)
                                str = str.substring(0, 7);
                            t.getCurve().getProbabilityLabel().setText(str);
                            if (s1.compareTo(s2) == 0)
                                t.getCurve().setArrowSelf(t.getCubicCurve().getControlX2(), t.getCubicCurve().getControlY2());
                            else {
                                t.getCurve().setArrow();
                            }
                        }
                    }
                } else {
                    o1 = t.getObsTo().getObservationName().getText();
                    for (String[] key : observationHashMap.keySet()) {
                        if ((key[0].contains(o1)) && (key[1].contains(s1))) {
                            t.setProbability(observationHashMap.get(key));
                            String str = Double.toString(t.getProbability());
                            if (str.contains("E"))
                                str = "0";
                            if (str.length() > 8)
                                str = str.substring(0, 7);

                            t.getCurve().getProbabilityLabel().setText(str);
                            t.getCurve().setArrow();

                        }
                    }
                }

            }

        }

    }

    public void expandNumbers(ActionEvent actionEvent) {
        for (StepProbability s : stepProbabilities) {
            String str = s.getLabel().getText();
            if (str.contains("E")) {
                str = "0";
            }

            s.getLabel().setText(str);
            updateStateNamePosition(s.getLabel(), s.getState());
        }
        for (State s : simulatorChain) {

            for (Transition t : s.getTransitions()) {
                String s1 = t.getFrom().getStateName().getText();
                String s2;
                String o1;
                if (t.getObsTo() == null) {
                    s2 = t.getTo().getStateName().getText();
                    for (String[] key : transitionHashMap.keySet()) {
                        if ((key[0].contains(s1)) && (key[1].contains(s2))) {
                            t.setProbability(transitionHashMap.get(key));
                            String str = Double.toString(t.getProbability());
                            if (str.contains("E"))
                                str = "0";
                            t.getCurve().getProbabilityLabel().setText(str);
                            if (s1.compareTo(s2) == 0)
                                t.getCurve().setArrowSelf(t.getCubicCurve().getControlX2(), t.getCubicCurve().getControlY2());
                            else {
                                t.getCurve().setArrow();
                            }
                        }
                    }
                } else {
                    o1 = t.getObsTo().getObservationName().getText();
                    for (String[] key : observationHashMap.keySet()) {
                        if ((key[0].contains(o1)) && (key[1].contains(s1))) {
                            t.setProbability(observationHashMap.get(key));
                            String str = Double.toString(t.getProbability());
                            if (str.contains("E"))
                                str = "0";

                            t.getCurve().getProbabilityLabel().setText(str);
                            t.getCurve().setArrow();

                        }
                    }
                }

            }

        }

    }




    enum Mode {
        SELECTION, NULL
    }

    ;
}
