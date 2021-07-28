package sample;

import controllers.MainController;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;

public class CubicCurveCreator {
    private LineDistanceManipulator lineDistanceManipulator;

    public CubicCurveCreator() {
        lineDistanceManipulator = new LineDistanceManipulator();
    }

    public CubicCurve createCubicCurveFromLine(Line line) {

        double distance = lineDistanceManipulator.getDistance(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
        double ratio = distance / 4;
        Line controlLine = lineDistanceManipulator.getLineAlteredFromBeginning(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY(), ratio);
        double controlX1 = controlLine.getStartX();
        double controlY1 = controlLine.getStartY();

        controlLine = lineDistanceManipulator.getLineAlteredFromBeginning(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY(), ratio * 3);
        double controlX2 = controlLine.getStartX();
        double controlY2 = controlLine.getStartY();


        CubicCurve cv = new CubicCurve(line.getStartX(), line.getStartY(), controlX1, controlY1, controlX2, controlY2, line.getEndX(), line.getEndY());
        cv.setStrokeWidth(MainController.lineWidth);
        cv.setFill(null);
        cv.setStroke(Color.BLACK);
        return cv;
    }

    public void straightenCurve(Transition t) {
        Line auxLine;
        if (t.getTo() != null) {
            auxLine = new Line(t.getFrom().getState().getCenterX(), t.getFrom().getState().getCenterY(), t.getTo().getState().getCenterX(), t.getTo().getState().getCenterY());
            auxLine = lineDistanceManipulator.getLineAlteredFromBeginning(auxLine.getStartX(), auxLine.getStartY(), auxLine.getEndX(), auxLine.getEndY(), MainController.circleRadius + 1);
            auxLine = lineDistanceManipulator.getLineAlteredFromEnd(auxLine.getStartX(), auxLine.getStartY(), auxLine.getEndX(), auxLine.getEndY(), MainController.circleRadius + 1);
        } else {
            auxLine = new Line(t.getFrom().getState().getCenterX(), t.getFrom().getState().getCenterY(), t.getObsTo().getOrigin().getX(), t.getObsTo().getOrigin().getY());
            auxLine = lineDistanceManipulator.getLineAlteredFromBeginning(auxLine.getStartX(), auxLine.getStartY(), auxLine.getEndX(), auxLine.getEndY(), MainController.circleRadius + 1);
            auxLine = lineDistanceManipulator.getLineAlteredFromEnd(auxLine.getStartX(), auxLine.getStartY(), auxLine.getEndX(), auxLine.getEndY(), lineDistanceManipulator.getDistanceToSquareBorder(auxLine.getStartX(), auxLine.getStartY(), auxLine.getEndX(), auxLine.getEndY()));

        }
        CubicCurve cv = createCubicCurveFromLine(auxLine);
        t.getCurve().getCubicCurve().setStartX(cv.getStartX());
        t.getCurve().getCubicCurve().setStartY(cv.getStartY());

        t.getCurve().getCubicCurve().setControlX1(cv.getControlX1());
        t.getCurve().getCubicCurve().setControlY1(cv.getControlY1());

        t.getCurve().getCubicCurve().setControlX2(cv.getControlX2());
        t.getCurve().getCubicCurve().setControlY2(cv.getControlY2());

        t.getCurve().getCubicCurve().setEndX(cv.getEndX());
        t.getCurve().getCubicCurve().setEndY(cv.getEndY());

        t.getCurve().getCnt1().setCenterX(t.getCubicCurve().getControlX1());
        t.getCurve().getCnt1().setCenterY(t.getCubicCurve().getControlY1());
        t.getCurve().getCnt2().setCenterX(t.getCubicCurve().getControlX2());
        t.getCurve().getCnt2().setCenterY(t.getCubicCurve().getControlY2());


        t.getCurve().setArrow();
        t.getCurve().updateLb();
        t.getCurve().setLineMode(true);


    }

}
