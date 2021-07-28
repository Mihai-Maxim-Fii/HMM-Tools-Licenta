package sample;

import controllers.MainController;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.List;

public class CurveDragPoints {

    private Pane mainDrawingPane;

    public CurveDragPoints(Pane mainDrawingPane) {
        this.mainDrawingPane = mainDrawingPane;
    }

    public void enableDragPoints(Curve curve, Transition t, List<State> states, List<Transition> currentlySelectedTransitions, List<State> currentlySelectedStates, Chain chain) {

        curve.getCubicCurve().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {


                if (chain != null) {
                    for (Observation o : chain.getObservations()) {
                        o.getObservation().setStroke(Color.BLACK);
                        o.getObservationName().setTextFill(Color.BLACK);

                    }

                }


                if (!mainDrawingPane.getChildren().contains(curve.getCnt1())) {
                    mainDrawingPane.getChildren().add(curve.getCnt1());
                }

                if (!mainDrawingPane.getChildren().contains(curve.getCnt2())) {
                    mainDrawingPane.getChildren().add(curve.getCnt2());
                }


                if (!mainDrawingPane.getChildren().contains(curve.lb1)) {

                    mainDrawingPane.getChildren().add(curve.lb1);
                    mainDrawingPane.getChildren().add(curve.lb2);
                    makeDragPointDragable(curve.getCnt1(), curve, t);
                    makeDragPointDragable(curve.getCnt2(), curve, t);
                    curve.selectionMode(true);
                    currentlySelectedTransitions.add(t);


                    for (State s : states) {
                        for (Transition t : s.getTransitions()) {

                            if (t.getCurve() != curve) {
                                if (mainDrawingPane.getChildren().contains(t.getCurve().getCnt1())) {
                                    mainDrawingPane.getChildren().removeAll(t.getCurve().getCnt1(), t.getCurve().getCnt2(), t.getCurve().lb1, t.getCurve().lb2);

                                    currentlySelectedTransitions.remove(t);
                                }
                                t.getCurve().selectionMode(false);

                            }
                        }

                    }
                    if (currentlySelectedStates.size() != 0) {
                        removeDragClickListeners(currentlySelectedStates.get(0));
                        currentlySelectedStates.get(0).getState().setStroke(Color.BLACK);
                        currentlySelectedStates.get(0).getStateName().setTextFill(Color.BLACK);
                        currentlySelectedStates.remove(currentlySelectedStates.get(0));
                    }


                    curve.lb1.setStartX(t.getFrom().getState().getCenterX());
                    curve.lb1.setStartY(t.getFrom().getState().getCenterY());
                    curve.lb1.setEndX(curve.getCnt1().getCenterX());
                    curve.lb1.setEndY(curve.getCnt1().getCenterY());
                    curve.lb1.setStrokeWidth(2);
                    curve.lb1.setStroke(Color.BLUE);


                    if (t.getObsTo() == null) {
                        curve.lb2.setStartX(t.getTo().getState().getCenterX());
                        curve.lb2.setStartY(t.getTo().getState().getCenterY());
                    } else {
                        curve.lb2.setStartX(t.getObsTo().getOrigin().getX());
                        curve.lb2.setStartY(t.getObsTo().getOrigin().getY());
                    }
                    curve.lb2.setEndX(curve.getCnt2().getCenterX());
                    curve.lb2.setEndY(curve.getCnt2().getCenterY());
                    curve.lb2.setStrokeWidth(2);
                    curve.lb2.setStroke(Color.BLUE);


                }

            }

        });
    }

    static class Delta {
        double x, y;
    }

    private double radius = 5;

    public void makeDragPointDragable(Circle circle, Curve curve, Transition t) {
        final MakeDraggableState.Delta dragDelta = new MakeDraggableState.Delta();

        circle.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // record a delta distance for the drag and drop operation.
                dragDelta.x = circle.getCenterX() - mouseEvent.getX();
                dragDelta.y = circle.getCenterY() - mouseEvent.getY();
                circle.getScene().setCursor(Cursor.MOVE);

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

                if ((mouseEvent.getX() + dragDelta.x <= mainDrawingPane.getWidth() - radius) && (mouseEvent.getY() + dragDelta.y <= mainDrawingPane.getHeight() - radius) && (mouseEvent.getY() + dragDelta.y >= radius) && (mouseEvent.getX() + dragDelta.x >= radius)) {
                    circle.setCenterX(mouseEvent.getX() + dragDelta.x);
                    circle.setCenterY(mouseEvent.getY() + dragDelta.y);
                }
                if (curve.getCnt1() == circle) {
                    curve.setLineMode(false);
                    curve.getCubicCurve().setControlX1(circle.getCenterX());
                    curve.getCubicCurve().setControlY1(circle.getCenterY());
                    LineDistanceManipulator ln = new LineDistanceManipulator();
                    Line l1;
                    if (t.getObsTo() != null)
                        l1 = ln.getLineAlteredFromBeginning(t.getFrom().getState().getCenterX(), t.getFrom().getState().getCenterY(), circle.getCenterX(), circle.getCenterY(), MainController.circleRadius + 1);
                    else
                        l1 = ln.getLineAlteredFromBeginning(t.getFrom().getState().getCenterX(), t.getFrom().getState().getCenterY(), circle.getCenterX(), circle.getCenterY(), MainController.circleRadius + 1);

                    curve.getCubicCurve().setStartX(l1.getStartX());
                    curve.getCubicCurve().setStartY(l1.getStartY());
                    curve.lb1.setEndX(circle.getCenterX());
                    curve.lb1.setEndY(circle.getCenterY());
                    curve.setArrow();


                } else {
                    curve.setLineMode(false);
                    curve.getCubicCurve().setControlX2(circle.getCenterX());
                    curve.getCubicCurve().setControlY2(circle.getCenterY());
                    LineDistanceManipulator ln = new LineDistanceManipulator();

                    Line l1;
                    if (t.getObsTo() != null)
                        l1 = ln.getLineAlteredFromBeginning(t.getObsTo().getOrigin().getX(), t.getObsTo().getOrigin().getY(), circle.getCenterX(), circle.getCenterY(), ln.getDistanceToSquareBorder(t.getObsTo().getOrigin().getX(), t.getObsTo().getOrigin().getY(), circle.getCenterX(), circle.getCenterY()));
                    else
                        l1 = ln.getLineAlteredFromBeginning(t.getTo().getState().getCenterX(), t.getTo().getState().getCenterY(), circle.getCenterX(), circle.getCenterY(), MainController.circleRadius + 1);

                    curve.getCubicCurve().setEndX(l1.getStartX());
                    curve.getCubicCurve().setEndY(l1.getStartY());
                    curve.lb2.setEndX(circle.getCenterX());
                    curve.lb2.setEndY(circle.getCenterY());
                    curve.setArrow();


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

    }

    public void removeDragClickListeners(State s) {
        s.getState().setOnMouseClicked(null);
        s.getState().setOnMouseReleased(null);
        s.getState().setOnMouseDragged(null);
        s.getState().setOnMouseEntered(null);
        s.getState().setOnMouseExited(null);


    }
}
