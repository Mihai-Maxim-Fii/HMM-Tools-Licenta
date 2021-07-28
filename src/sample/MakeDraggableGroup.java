package sample;

import controllers.MainController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;


public class MakeDraggableGroup {
    private double radius;
    private Pane mainDrawingPane;
    private IncidenceMatrixChain incidenceMatrixChain = null;

    public MakeDraggableGroup(Pane mainDrawingPane, double radius, IncidenceMatrixChain incidenceMatrixChain) {
        this.incidenceMatrixChain = incidenceMatrixChain;

        this.radius = radius;
        this.mainDrawingPane = mainDrawingPane;
    }

    static class Delta {
        double x, y;
    }

    public void deleteChain(Chain currentChain,List<State> stateList) {
        List<State> states = new ArrayList<>();
        List<Observation> observations = new ArrayList<>();
        for (State st : stateList) {
            states.add(st);
            mainDrawingPane.getChildren().removeAll(st.getState(), st.getStateName(), st.getTestLine());
            for (Transition t : st.getTransitions()) {
                mainDrawingPane.getChildren().removeAll(t.getCubicCurve(), t.getCurve().getProbabilityLabel(), t.getCurve().getArrow());
                if (t.getObsTo() != null) {
                    if (!observations.contains(t.getObsTo())) {
                        observations.add(t.getObsTo());
                    }
                    mainDrawingPane.getChildren().removeAll(t.getObsTo().getObservation(), t.getObsTo().getObservationName());
                }

            }
        }
        currentChain.getStates().removeAll(states);
        currentChain.getObservations().removeAll(observations);
    }

    public boolean checkValidity(List<State> states) {
        for (State s : states) {
            if (s.getState().getStroke().toString().contains("0xff0000ff")) {
                return false;

            }
            for (Transition t : s.getTransitions()) {
                if (t.getCubicCurve().getStroke().toString().contains("0xff0000ff"))
                    return false;
                if (t.getCurve().getProbabilityLabel().getTextFill().toString().contains("0xff0000ff"))
                    return false;
                if (t.getObsTo() != null) {
                    if (t.getObsTo().getObservation().getStroke().toString().contains("0xff0000ff"))
                        return false;
                    if (t.getObsTo().getObservationName().getTextFill().toString().contains("0xff0000ff"))
                        return false;
                }
            }
        }

        return true;
    }

    public void enableDrag(final Circle circle, State s, Chain currentChain, List<State> states) {
        final MakeDraggableState.Delta dragDelta = new MakeDraggableState.Delta();

        circle.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.SECONDARY) {

                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem menuItem1 = new MenuItem("Send To Simulator");
                    if (checkValidity(states) == false)
                        menuItem1.setDisable(true);
                    MenuItem menuItem2 = new MenuItem("Delete");
                    menuItem1.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            SimulatorHandler simulatorHandler = new SimulatorHandler();

                            simulatorHandler.openInSimulator(states, incidenceMatrixChain);
                        }
                    });

                    menuItem2.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {

                            deleteChain(currentChain,states);
                        }
                    });


                    contextMenu.getItems().addAll(menuItem1, menuItem2);
                    s.getStateName().setContextMenu(contextMenu);
                    contextMenu.show(mainDrawingPane, mouseEvent.getScreenX(), mouseEvent.getScreenY());

                }

            }
        });


        circle.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                circle.getScene().setCursor(Cursor.HAND);
            }
        });
        circle.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                double changeInX, changeInY;
                changeInX = mouseEvent.getX() - circle.getCenterX();
                changeInY = mouseEvent.getY() - circle.getCenterY();
                boolean drag = true;

                List<Observation> observations = new ArrayList<>();

                for (State st : states) {

                    for (Transition t : st.getTransitions()) {

                        if (t.getObsTo() != null) {
                            if ((t.getObsTo().getOrigin().getY() + changeInY > mainDrawingPane.getHeight() - MainController.observationSide / 2)) {
                                mainDrawingPane.setPrefHeight(mainDrawingPane.getHeight() + 200);
                            }

                            if ((t.getObsTo().getOrigin().getX() + changeInX > mainDrawingPane.getWidth() - MainController.observationSide / 2)) {
                                mainDrawingPane.setPrefWidth(mainDrawingPane.getWidth() + 200);
                            }
                            if (!observations.contains(t.getObsTo()))
                                observations.add(t.getObsTo());
                        }
                    }


                    if ((st.getState().getCenterY() + changeInY > mainDrawingPane.getHeight() - radius)) {
                        mainDrawingPane.setPrefHeight(mainDrawingPane.getHeight() + 200);
                    }

                    if ((st.getState().getCenterX() + changeInX > mainDrawingPane.getWidth() - radius)) {
                        mainDrawingPane.setPrefWidth(mainDrawingPane.getWidth() + 200);
                    }

                    if (!((st.getState().getCenterY() + changeInY >= radius) && (st.getState().getCenterX() + changeInX >= radius))) {
                        drag = false;
                    }
                }
                for (Observation obs : observations) {
                    if (!(((obs.getOrigin().getY() + changeInY > MainController.observationSide / 2)) && (obs.getOrigin().getX() + changeInX > MainController.observationSide / 2))) {
                        drag = false;
                    }
                }

                if (drag) {
                    if ((mouseEvent.getX() <= mainDrawingPane.getWidth() - radius) && (mouseEvent.getY() <= mainDrawingPane.getHeight() - radius) && (mouseEvent.getY() >= radius) && (mouseEvent.getX() >= radius)) {
                        circle.setCenterX(mouseEvent.getX());
                        circle.setCenterY(mouseEvent.getY());
                    }
                    MakeDraggableState makeDraggableState = new MakeDraggableState(mainDrawingPane.getWidth(), mainDrawingPane.getHeight(), radius);

                    s.getTestLine().setStartX(s.getState().getCenterX());
                    s.getTestLine().setStartY(s.getState().getCenterY());
                    s.getTestLine().setEndX(s.getState().getCenterX());
                    s.getTestLine().setEndY(s.getState().getCenterY());
                    makeDraggableState.renewTransitions(s, currentChain, mainDrawingPane, changeInX, changeInY);

                    s.updateStateNamePosition();


                    for (Observation obs : observations) {
                        obs.getOrigin().setX(obs.getOrigin().getX() + changeInX);
                        obs.getOrigin().setY(obs.getOrigin().getY() + changeInY);
                        obs.getObservation().setX(obs.getOrigin().getX() - MainController.observationSide / 2);
                        obs.getObservation().setY(obs.getOrigin().getY() - MainController.observationSide / 2);
                        obs.updateObservationNamePosition();
                        for (State st : obs.getStates()) {
                            makeDraggableState.renewTransitionsObservation(st, obs, currentChain, mainDrawingPane, changeInX, changeInY);
                        }

                    }

                    for (State st : states) {
                        if (s != st) {
                            st.getState().setCenterX(st.getState().getCenterX() + changeInX);
                            st.getState().setCenterY(st.getState().getCenterY() + changeInY);
                            st.getTestLine().setStartX(st.getState().getCenterX());
                            st.getTestLine().setStartY(st.getState().getCenterY());
                            st.getTestLine().setEndX(st.getState().getCenterX());
                            st.getTestLine().setEndY(st.getState().getCenterY());
                            st.updateStateNamePosition();


                            makeDraggableState.renewTransitions(st, currentChain, mainDrawingPane, changeInX, changeInY);


                        }
                    }


                }


            }
        });
        circle.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    circle.getScene().setCursor(Cursor.HAND);
                }
            }
        });
        circle.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    circle.getScene().setCursor(Cursor.DEFAULT);
                }
            }
        });


        s.getStateName().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });


    }


}
