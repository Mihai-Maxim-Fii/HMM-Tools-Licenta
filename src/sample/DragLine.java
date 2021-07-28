package sample;

import controllers.MainController;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class DragLine extends MainController {
    private double width, height, radius;


    Pane mainDrawingPane;
    Chain currentChain;

    public DragLine(double width, double height, double radius, Pane myDrawingPane, Chain currentChain) {
        this.width = width;
        this.height = height;
        this.radius = radius;
        this.mainDrawingPane = myDrawingPane;
        this.currentChain = currentChain;
    }

    private void restoreTestLinesAndDragListeners() {

        for (State s : currentChain.getStates()) {
            if (!mainDrawingPane.getChildren().contains(s.getTestLine()))
                mainDrawingPane.getChildren().add(s.getTestLine());
            enableDrag(s.getTestLine());
        }
    }

    public void restoreTestLine(Line line, double originX, double originY) {
        line.setStartX(originX);
        line.setStartY(originY);
        line.setEndX(originX);
        line.setEndY(originY);
        line.setStrokeWidth(MainController.circleRadius * 1.8);
        line.setStroke(Color.WHITE);
        line.setStrokeLineCap(StrokeLineCap.ROUND);

    }


    public void enableDrag(final Line line) {
        final MakeDraggableState.Delta dragDelta = new MakeDraggableState.Delta();

        line.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // record a delta distance for the drag and drop operation.
                dragDelta.x = mouseEvent.getX();
                dragDelta.y = mouseEvent.getY();
                line.getScene().setCursor(Cursor.MOVE);
                mainDrawingPane.getChildren().remove(line);
                mainDrawingPane.getChildren().add(line);
                for (State s : currentChain.getStates()) {
                    if (s.getTestLine() != line) {
                        MainController.removeDragClickListenersFromLine(s.getTestLine());
                        mainDrawingPane.getChildren().remove(s.getTestLine());
                    }
                    mainDrawingPane.getChildren().remove(s.getStateName());
                    mainDrawingPane.getChildren().add(s.getStateName());
                }


            }
        });


        line.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                line.getScene().setCursor(Cursor.HAND);

                State s1 = null;
                State s2 = null;
                Observation observation = null;
                for (State s : currentChain.getStates()) {
                    if (getDistanceBetweenTwoPoints(s.getState().getCenterX(), s.getState().getCenterY(), line.getStartX(), line.getStartY()) <= MainController.circleRadius) {
                        s1 = s;
                    }
                    if (getDistanceBetweenTwoPoints(s.getState().getCenterX(), s.getState().getCenterY(), line.getEndX(), line.getEndY()) <= MainController.circleRadius) {
                        s2 = s;
                    }
                    for (Observation obs : currentChain.getObservations()) {
                        if (getDistanceBetweenTwoPoints(obs.getOrigin().getX(), obs.getOrigin().getY(), line.getEndX(), line.getEndY()) <= MainController.observationSide / 2) {
                            observation = obs;
                        }
                    }

                    if ((s1 != null) && (s2 != null) && (s1 != s2)) {
                        for (Transition t : s1.getTransitions()) {
                            if (t.getTo() == s2) {
                                restoreTestLine(line, s1.getState().getCenterX(), s1.getState().getCenterY());
                                restoreTestLinesAndDragListeners();


                                for (State f : currentChain.getStates()) {
                                    mainDrawingPane.getChildren().remove(f.getStateName());
                                    mainDrawingPane.getChildren().add(f.getStateName());
                                }
                                return;
                            }

                        }

                        LineDistanceManipulator lineDistanceManipulator = new LineDistanceManipulator();
                        Line transitionLine = lineDistanceManipulator.getLineAlteredFromBeginning(s1.getState().getCenterX(), s1.getState().getCenterY(), s2.getState().getCenterX(), s2.getState().getCenterY(), MainController.circleRadius);
                        transitionLine = lineDistanceManipulator.getLineAlteredFromEnd(transitionLine.getStartX(), transitionLine.getStartY(), transitionLine.getEndX(), transitionLine.getEndY(), MainController.circleRadius);
                        transitionLine.setStrokeWidth(MainController.lineWidth);


                        MainController.removeDragClickListenersFromLine(line);
                        restoreTestLine(line, s1.getState().getCenterX(), s1.getState().getCenterY());
                        mainDrawingPane.getChildren().remove(line);
                        Transition newTransition = new Transition(s1, s2, -1, transitionLine);
                        newTransition.getCurve().setArrow();
                        mainDrawingPane.getChildren().add(newTransition.getCubicCurve());
                        mainDrawingPane.getChildren().add(newTransition.getCurve().getArrow());


                        //newTransition.getCurve().addCircles(mainDrawingPane);
                        mainDrawingPane.getChildren().add(newTransition.getCurve().getProbabilityLabel());
                        ProbabilityHandler probabilityHandler = new ProbabilityHandler();
                        if (!s1.getTransitions().contains(newTransition)) {
                            s1.getTransitions().add(newTransition);
                            probabilityHandler.enableOnClickProbability(newTransition);
                        }


                    } else if ((s1 != null) && (observation != null)) {

                        for (Transition t : s1.getTransitions()) {
                            if (t.getObsTo() == observation) {
                                restoreTestLine(line, s1.getState().getCenterX(), s1.getState().getCenterY());
                                restoreTestLinesAndDragListeners();


                                for (State f : currentChain.getStates()) {
                                    mainDrawingPane.getChildren().remove(f.getStateName());
                                    mainDrawingPane.getChildren().add(f.getStateName());
                                }
                                return;
                            }

                        }

                        LineDistanceManipulator lineDistanceManipulator = new LineDistanceManipulator();
                        Line transitionLine = lineDistanceManipulator.getLineAlteredFromBeginning(s1.getState().getCenterX(), s1.getState().getCenterY(), observation.getOrigin().getX(), observation.getOrigin().getY(), MainController.circleRadius);
                        transitionLine = lineDistanceManipulator.getLineAlteredFromEnd(transitionLine.getStartX(), transitionLine.getStartY(), transitionLine.getEndX(), transitionLine.getEndY(), lineDistanceManipulator.getDistanceToSquareBorder(observation.getOrigin().getX(), observation.getOrigin().getY(), line.getStartX(), line.getStartY()));
                        transitionLine.setStrokeWidth(MainController.lineWidth);


                        MainController.removeDragClickListenersFromLine(line);
                        restoreTestLine(line, s1.getState().getCenterX(), s1.getState().getCenterY());
                        mainDrawingPane.getChildren().remove(line);
                        Transition newTransition = new Transition(s1, observation, -1, transitionLine);
                        newTransition.getCurve().setArrow();
                        newTransition.getCurve().getCubicCurve().getStrokeDashArray().addAll(10d, 10d);
                        mainDrawingPane.getChildren().add(newTransition.getCubicCurve());
                        mainDrawingPane.getChildren().add(newTransition.getCurve().getArrow());


                        //newTransition.getCurve().addCircles(mainDrawingPane);
                        mainDrawingPane.getChildren().add(newTransition.getCurve().getProbabilityLabel());
                        ProbabilityHandler probabilityHandler = new ProbabilityHandler();
                        if (!s1.getTransitions().contains(newTransition)) {
                            s1.getTransitions().add(newTransition);
                            newTransition.getObsTo().getStates().add(s1);
                            probabilityHandler.enableOnClickProbability(newTransition);
                        }


                    }

                }


                restoreTestLinesAndDragListeners();
                restoreTestLine(line, s1.getState().getCenterX(), s1.getState().getCenterY());

                for (State s : currentChain.getStates()) {
                    mainDrawingPane.getChildren().remove(s.getStateName());
                    mainDrawingPane.getChildren().add(s.getStateName());
                }


            }

        });
        line.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                line.setStrokeWidth(MainController.lineWidth);
                line.setBlendMode(BlendMode.SRC_OVER);
                line.setStroke(Color.BLACK);

                if ((mouseEvent.getX() <= width - radius) && (mouseEvent.getY() <= height - radius) && (mouseEvent.getY() >= radius) && (mouseEvent.getX() >= radius)) {
                    line.setStartX(dragDelta.x);
                    line.setStartY(dragDelta.y);
                    line.setEndX(mouseEvent.getX());
                    line.setEndY(mouseEvent.getY());
                }


            }
        });
        line.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    line.getScene().setCursor(Cursor.HAND);
                }
            }
        });
        line.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    line.getScene().setCursor(Cursor.DEFAULT);

                }
            }
        });
    }
}
