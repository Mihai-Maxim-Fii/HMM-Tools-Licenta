package sample;

import controllers.MainController;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;

public class MakeDraggableState {


    private double width, height, radius;

    public MakeDraggableState(double width, double height, double radius) {
        this.width = width;
        this.height = height;
        this.radius = radius;
    }


    static class Delta {
        double x, y;
    }
    // make a node movable by dragging it around with the mouse.

    public static double getDistanceBetweenTwoPoints(double x1, double y1, double x2, double y2) {
        return Math.sqrt((Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));
    }

    public void enableDrag(final Circle circle, State s, Chain currentChain, Pane mainDrawingPane) {
        final Delta dragDelta = new Delta();

        circle.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.SECONDARY) {


                }
                // record a delta distance for the drag and drop operation.
                dragDelta.x = circle.getCenterX();
                dragDelta.y = circle.getCenterY();
                // circle.getScene().setCursor(Cursor.MOVE);
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

                if ((mouseEvent.getY() > mainDrawingPane.getHeight() - radius)) {
                    mainDrawingPane.setPrefHeight(mainDrawingPane.getHeight() + 200);
                }

                if ((mouseEvent.getX() > mainDrawingPane.getWidth() - radius)) {
                    mainDrawingPane.setPrefWidth(mainDrawingPane.getWidth() + 200);
                }

                double origX = 0, origY = 0;
                if ((mouseEvent.getY() >= radius) && (mouseEvent.getX() >= radius)) {
                    origX = mouseEvent.getX() - circle.getCenterX();
                    origY = mouseEvent.getY() - circle.getCenterY();
                    circle.setCenterX(mouseEvent.getX());
                    circle.setCenterY(mouseEvent.getY());
                }
                s.getTestLine().setStartX(s.getState().getCenterX());
                s.getTestLine().setStartY(s.getState().getCenterY());
                s.getTestLine().setEndX(s.getState().getCenterX());
                s.getTestLine().setEndY(s.getState().getCenterY());
                renewTransitions(s, currentChain, mainDrawingPane, origX, origY);

                s.updateStateNamePosition();
                mainDrawingPane.getChildren().remove(s.getStateName());
                mainDrawingPane.getChildren().add(s.getStateName());


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

    public void renewTransitionsObservation(State s, Observation observation, Chain currentChain, Pane mainDrawingPane, Double origX, Double origY) {
        for (Transition t : s.getTransitions()) {

            if (t.getObsTo() == observation) {
                mainDrawingPane.getChildren().removeAll(t.getCurve().lb2, t.getCurve().lb1, t.getCurve().getCnt1(), t.getCurve().getCnt2());

                if (!t.getCurve().isLineMode()) {


                    LineDistanceManipulator lineDistanceManipulator = new LineDistanceManipulator();
                    Line transitionLine;
                    if (t.getObsTo() == null) {
                        transitionLine = lineDistanceManipulator.getLineAlteredFromBeginning(t.getFrom().getState().getCenterX(), t.getFrom().getState().getCenterY(), t.getTo().getState().getCenterX(), t.getTo().getState().getCenterY(), MainController.circleRadius);
                        transitionLine = lineDistanceManipulator.getLineAlteredFromEnd(transitionLine.getStartX(), transitionLine.getStartY(), transitionLine.getEndX(), transitionLine.getEndY(), MainController.circleRadius);
                    } else {
                        transitionLine = lineDistanceManipulator.getLineAlteredFromBeginning(t.getFrom().getState().getCenterX(), t.getFrom().getState().getCenterY(), t.getObsTo().getOrigin().getX(), t.getObsTo().getOrigin().getY(), MainController.circleRadius);
                        transitionLine = lineDistanceManipulator.getLineAlteredFromEnd(transitionLine.getStartX(), transitionLine.getStartY(), transitionLine.getEndX(), transitionLine.getEndY(), lineDistanceManipulator.getDistanceToSquareBorder(transitionLine.getStartX(), transitionLine.getStartY(), transitionLine.getEndX(), transitionLine.getEndY()));
                    }

                    t.getLine().setStartX(transitionLine.getStartX());
                    t.getLine().setStartY(transitionLine.getStartY());
                    t.getLine().setEndX(transitionLine.getEndX());
                    t.getLine().setEndY(transitionLine.getEndY());

                    CubicCurveCreator cv = new CubicCurveCreator();
                    CubicCurve newCurve = cv.createCubicCurveFromLine(t.getLine());

                    t.getCubicCurve().setStartX(t.getLine().getStartX());
                    t.getCubicCurve().setStartY(t.getLine().getStartY());
                    t.getCubicCurve().setEndX(t.getLine().getEndX());
                    t.getCubicCurve().setEndY(t.getLine().getEndY());
                    //t.setCubicCurve(newCurve);
                    //t.setCubicCurve(t.getCubicCurve());


                    t.getCubicCurve().setControlX2(t.getCubicCurve().getControlX2() + origX);
                    t.getCubicCurve().setControlY2(t.getCubicCurve().getControlY2() + origY);

                    LineDistanceManipulator ln = new LineDistanceManipulator();
                    Line l1;
                    l1 = ln.getLineAlteredFromBeginning(t.getObsTo().getOrigin().getX(), t.getObsTo().getOrigin().getY(), t.getCnt2().getCenterX(), t.getCnt2().getCenterY(), lineDistanceManipulator.getDistanceToSquareBorder(t.getObsTo().getOrigin().getX(), t.getObsTo().getOrigin().getY(), t.getCnt2().getCenterX(), t.getCnt2().getCenterY()));
                    t.getCubicCurve().setEndX(l1.getStartX());
                    t.getCubicCurve().setEndY(l1.getStartY());


                    l1 = ln.getLineAlteredFromBeginning(t.getFrom().getState().getCenterX(), t.getFrom().getState().getCenterY(), t.getCnt1().getCenterX(), t.getCnt1().getCenterY(), MainController.circleRadius + 1);
                    t.getCubicCurve().setStartX(l1.getStartX());
                    t.getCubicCurve().setStartY(l1.getStartY());


                    t.getCurve().lb2.setStartX(t.getObsTo().getOrigin().getX());
                    t.getCurve().lb2.setStartY(t.getObsTo().getOrigin().getY());
                    t.getCurve().lb2.setEndX(t.getCurve().getCnt2().getCenterX());
                    t.getCurve().lb2.setEndY(t.getCurve().getCnt2().getCenterY());
                    t.setCubicCurve(t.getCubicCurve());


                } else {

                    CubicCurveCreator cubicCurveCreator = new CubicCurveCreator();
                    cubicCurveCreator.straightenCurve(t);

                }

            }
        }
    }

    public void renewTransitions(State s, Chain currentChain, Pane mainDrawingPane, Double origX, Double origY) {

        for (Transition t : s.getTransitions()) {

            if (t.getFrom() != t.getTo()) {


                if (!t.getCurve().isLineMode()) {

                    LineDistanceManipulator lineDistanceManipulator = new LineDistanceManipulator();
                    Line transitionLine;
                    if (t.getObsTo() == null) {
                        transitionLine = lineDistanceManipulator.getLineAlteredFromBeginning(t.getFrom().getState().getCenterX(), t.getFrom().getState().getCenterY(), t.getTo().getState().getCenterX(), t.getTo().getState().getCenterY(), MainController.circleRadius);
                        transitionLine = lineDistanceManipulator.getLineAlteredFromEnd(transitionLine.getStartX(), transitionLine.getStartY(), transitionLine.getEndX(), transitionLine.getEndY(), MainController.circleRadius);
                    } else {
                        transitionLine = lineDistanceManipulator.getLineAlteredFromBeginning(t.getFrom().getState().getCenterX(), t.getFrom().getState().getCenterY(), t.getObsTo().getOrigin().getX(), t.getObsTo().getOrigin().getY(), MainController.circleRadius);
                        transitionLine = lineDistanceManipulator.getLineAlteredFromEnd(transitionLine.getStartX(), transitionLine.getStartY(), transitionLine.getEndX(), transitionLine.getEndY(), lineDistanceManipulator.getDistanceToSquareBorder(transitionLine.getStartX(), transitionLine.getStartY(), transitionLine.getEndX(), transitionLine.getEndY()));
                    }

                    t.getLine().setStartX(transitionLine.getStartX());
                    t.getLine().setStartY(transitionLine.getStartY());
                    t.getLine().setEndX(transitionLine.getEndX());
                    t.getLine().setEndY(transitionLine.getEndY());

                    CubicCurveCreator cv = new CubicCurveCreator();
                    CubicCurve newCurve = cv.createCubicCurveFromLine(t.getLine());

                    t.getCubicCurve().setStartX(t.getLine().getStartX());
                    t.getCubicCurve().setStartY(t.getLine().getStartY());
                    t.getCubicCurve().setEndX(t.getLine().getEndX());
                    t.getCubicCurve().setEndY(t.getLine().getEndY());
                    //t.setCubicCurve(newCurve);
                    //t.setCubicCurve(t.getCubicCurve());


                    LineDistanceManipulator ln = new LineDistanceManipulator();
                    Line l1 = ln.getLineAlteredFromBeginning(t.getFrom().getState().getCenterX(), t.getFrom().getState().getCenterY(), t.getCnt1().getCenterX(), t.getCnt1().getCenterY(), MainController.circleRadius + 1);
                    t.getCubicCurve().setStartX(l1.getStartX());
                    t.getCubicCurve().setStartY(l1.getStartY());

                    t.getCubicCurve().setControlX1(t.getCubicCurve().getControlX1() + origX);
                    t.getCubicCurve().setControlY1(t.getCubicCurve().getControlY1() + origY);


                    if (t.getObsTo() == null)
                        l1 = ln.getLineAlteredFromBeginning(t.getTo().getState().getCenterX(), t.getTo().getState().getCenterY(), t.getCnt2().getCenterX(), t.getCnt2().getCenterY(), MainController.circleRadius + 1);
                    else {
                        l1 = ln.getLineAlteredFromBeginning(t.getObsTo().getOrigin().getX(), t.getObsTo().getOrigin().getY(), t.getCnt2().getCenterX(), t.getCnt2().getCenterY(), lineDistanceManipulator.getDistanceToSquareBorder(t.getObsTo().getOrigin().getX(), t.getObsTo().getOrigin().getY(), t.getCnt2().getCenterX(), t.getCnt2().getCenterY()));
                    }
                    t.getCubicCurve().setEndX(l1.getStartX());
                    t.getCubicCurve().setEndY(l1.getStartY());


                    t.getCurve().lb1.setStartX(t.getFrom().getState().getCenterX());
                    t.getCurve().lb1.setStartY(t.getFrom().getState().getCenterY());


                    t.setCubicCurve(t.getCubicCurve());


                } else {

                    CubicCurveCreator cubicCurveCreator = new CubicCurveCreator();
                    cubicCurveCreator.straightenCurve(t);

                }


            } else {
                double minDist = Integer.MAX_VALUE;
                double minDistX, minDistY;
                minDistX = t.getFrom().getState().getCenterX();
                minDistY = t.getFrom().getState().getCenterY();
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

                if ((t.getFrom().getState().getCenterX() - MainController.selfTransitionSize > 0) && (t.getFrom().getState().getCenterY() - MainController.selfTransitionSize > 0)) {
                    double newDist = 0;
                    newDist += getDistanceBetweenTwoPoints((t.getFrom().getState().getCenterX() + t.getFrom().getState().getCenterX() - MainController.selfTransitionSize) / 2, (t.getFrom().getState().getCenterY() - MainController.selfTransitionSize + t.getFrom().getState().getCenterY()) / 2, minDistX, minDistY);
                    if (newDist > minDist) {
                        t.getCubicCurve().setControlY1(t.getFrom().getState().getCenterY() - MainController.selfTransitionSize);
                        t.getCubicCurve().setControlX2(t.getFrom().getState().getCenterX() - MainController.selfTransitionSize);
                        t.getCubicCurve().setStartY(t.getFrom().getState().getCenterY() - MainController.circleRadius);
                        t.getCubicCurve().setEndX(t.getFrom().getState().getCenterX() - MainController.circleRadius);
                        minDist = newDist;
                    }

                }

                if ((t.getFrom().getState().getCenterX() + MainController.selfTransitionSize < mainDrawingPane.getWidth()) && (t.getFrom().getState().getCenterY() - MainController.selfTransitionSize > 0)) {
                    double newDist = 0;
                    newDist += getDistanceBetweenTwoPoints((t.getFrom().getState().getCenterX() + t.getFrom().getState().getCenterX() + MainController.selfTransitionSize) / 2, (t.getFrom().getState().getCenterY() - MainController.selfTransitionSize + t.getFrom().getState().getCenterY()) / 2, minDistX, minDistY);
                    if (newDist > minDist) {
                        t.getCubicCurve().setControlY1(t.getFrom().getState().getCenterY() - MainController.selfTransitionSize);
                        t.getCubicCurve().setControlX2(t.getFrom().getState().getCenterX() + MainController.selfTransitionSize);
                        t.getCubicCurve().setStartY(t.getFrom().getState().getCenterY() - MainController.circleRadius);
                        t.getCubicCurve().setEndX(t.getFrom().getState().getCenterX() + MainController.circleRadius);
                        minDist = newDist;
                    }


                }

                if ((t.getFrom().getState().getCenterX() - MainController.selfTransitionSize > 0) && (t.getFrom().getState().getCenterY() + MainController.selfTransitionSize > 0)) {
                    double newDist = 0;
                    newDist += getDistanceBetweenTwoPoints((t.getFrom().getState().getCenterX() + t.getFrom().getState().getCenterX() - MainController.selfTransitionSize) / 2, (t.getFrom().getState().getCenterY() + MainController.selfTransitionSize + t.getFrom().getState().getCenterY()) / 2, minDistX, minDistY);
                    if (newDist > minDist) {
                        t.getCubicCurve().setControlY1(t.getFrom().getState().getCenterY() + MainController.selfTransitionSize);
                        t.getCubicCurve().setControlX2(t.getFrom().getState().getCenterX() - MainController.selfTransitionSize);
                        t.getCubicCurve().setStartY(t.getFrom().getState().getCenterY() + MainController.circleRadius);
                        t.getCubicCurve().setEndX(t.getFrom().getState().getCenterX() - MainController.circleRadius);
                        minDist = newDist;
                    }


                }

                if ((t.getFrom().getState().getCenterX() + MainController.selfTransitionSize > 0) && (t.getFrom().getState().getCenterY() + MainController.selfTransitionSize > 0)) {
                    double newDist = 0;
                    newDist += getDistanceBetweenTwoPoints((t.getFrom().getState().getCenterX() + t.getFrom().getState().getCenterX() + MainController.selfTransitionSize) / 2, (t.getFrom().getState().getCenterY() + MainController.selfTransitionSize + t.getFrom().getState().getCenterY()) / 2, minDistX, minDistY);
                    if (newDist > minDist) {
                        t.getCubicCurve().setControlY1(t.getFrom().getState().getCenterY() + MainController.selfTransitionSize);
                        t.getCubicCurve().setControlX2(t.getFrom().getState().getCenterX() + MainController.selfTransitionSize);
                        t.getCubicCurve().setStartY(t.getFrom().getState().getCenterY() + MainController.circleRadius);
                        t.getCubicCurve().setEndX(t.getFrom().getState().getCenterX() + MainController.circleRadius);
                        minDist = newDist;
                    }
                }


                t.getCubicCurve().setControlX1(t.getFrom().getState().getCenterX());
                t.getCubicCurve().setControlY2(t.getFrom().getState().getCenterY());

                t.getCubicCurve().setStartX(t.getFrom().getState().getCenterX());

                t.getCubicCurve().setEndY(t.getFrom().getState().getCenterY());

                t.getCurve().getCnt1().setCenterX(t.getCubicCurve().getControlX1());
                t.getCurve().getCnt1().setCenterY(t.getCubicCurve().getControlY1());

                t.getCurve().getCnt2().setCenterX(t.getCubicCurve().getControlX2());
                t.getCurve().getCnt2().setCenterY(t.getCubicCurve().getControlY2());
                t.getCurve().lb1.setStartX(t.getFrom().getState().getCenterX());
                t.getCurve().lb1.setStartY(t.getFrom().getState().getCenterY());
                t.getCurve().setArrowSelf(t.getCubicCurve().getControlX2(), t.getCubicCurve().getControlY2());


            }


        }

        for (State s1 : currentChain.getStates()) {
            for (Transition t : s1.getTransitions()) {
                if ((t.getTo() == s) && (t.getFrom() != s) && (t.getObsTo() == null)) {

                    if (!t.getCurve().isLineMode()) {

                        LineDistanceManipulator lineDistanceManipulator = new LineDistanceManipulator();
                        Line transitionLine = lineDistanceManipulator.getLineAlteredFromBeginning(t.getFrom().getState().getCenterX(), t.getFrom().getState().getCenterY(), t.getTo().getState().getCenterX(), t.getTo().getState().getCenterY(), MainController.circleRadius);
                        transitionLine = lineDistanceManipulator.getLineAlteredFromEnd(transitionLine.getStartX(), transitionLine.getStartY(), transitionLine.getEndX(), transitionLine.getEndY(), MainController.circleRadius);
                        t.getLine().setStartX(transitionLine.getStartX());
                        t.getLine().setStartY(transitionLine.getStartY());
                        t.getLine().setEndX(transitionLine.getEndX());
                        t.getLine().setEndY(transitionLine.getEndY());

                        CubicCurveCreator cv = new CubicCurveCreator();
                        CubicCurve newCurve = cv.createCubicCurveFromLine(t.getLine());

                        t.getCubicCurve().setStartX(t.getLine().getStartX());
                        t.getCubicCurve().setStartY(t.getLine().getStartY());
                        t.getCubicCurve().setEndX(t.getLine().getEndX());
                        t.getCubicCurve().setEndY(t.getLine().getEndY());
                        // t.setCubicCurve(newCurve);
                        t.setCubicCurve(t.getCubicCurve());


                        t.getCubicCurve().setControlX2(t.getCubicCurve().getControlX2() + origX);
                        t.getCubicCurve().setControlY2(t.getCubicCurve().getControlY2() + origY);

                        LineDistanceManipulator ln = new LineDistanceManipulator();
                        Line l1 = ln.getLineAlteredFromBeginning(t.getTo().getState().getCenterX(), t.getTo().getState().getCenterY(), t.getCnt2().getCenterX(), t.getCnt2().getCenterY(), MainController.circleRadius + 1);
                        t.getCubicCurve().setEndX(l1.getStartX());
                        t.getCubicCurve().setEndY(l1.getStartY());


                        l1 = ln.getLineAlteredFromBeginning(t.getFrom().getState().getCenterX(), t.getFrom().getState().getCenterY(), t.getCnt1().getCenterX(), t.getCnt1().getCenterY(), MainController.circleRadius + 1);
                        t.getCubicCurve().setStartX(l1.getStartX());
                        t.getCubicCurve().setStartY(l1.getStartY());


                        t.getCurve().lb2.setStartX(t.getTo().getState().getCenterX());
                        t.getCurve().lb2.setStartY(t.getTo().getState().getCenterY());
                        t.setCubicCurve(t.getCubicCurve());


                    } else {
                        CubicCurveCreator cubicCurveCreator = new CubicCurveCreator();
                        cubicCurveCreator.straightenCurve(t);
                    }


                }

            }
        }


    }
}
