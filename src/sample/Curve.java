package sample;

import controllers.MainController;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;

public class Curve {
    private CubicCurve cubicCurve;
    private Circle cnt1, cnt2;
    public Line lb1, lb2;
    private Polygon arrow;
    private Circle c1, c2;


    private boolean selectedMode = false;

    public boolean isLineMode() {
        return lineMode;
    }

    public void setLineMode(boolean lineMode) {
        this.lineMode = lineMode;
    }

    private boolean lineMode = true;

    public Label getProbabilityLabel() {
        return probabilityLabel;
    }

    private Label probabilityLabel;

    public Curve() {

    }


    public double[] getPointOnCurve(double t, CubicCurve cubicCurve) {
        double BezierX = (Math.pow(1.0 - t, 3)) * cubicCurve.getStartX() + 3 * (Math.pow(1.0 - t, 2)) * t * cubicCurve.getControlX1() + 3 * (Math.pow(1.0 - t, 1)) * Math.pow(t, 2) * cubicCurve.getControlX2() + (Math.pow(t, 3)) * cubicCurve.getEndX();
        double BezierY = (Math.pow(1.0 - t, 3)) * cubicCurve.getStartY() + 3 * (Math.pow(1.0 - t, 2)) * t * cubicCurve.getControlY1() + 3 * (Math.pow(1.0 - t, 1)) * Math.pow(t, 2) * cubicCurve.getControlY2() + (Math.pow(t, 3)) * cubicCurve.getEndY();
        double d[] = {BezierX, BezierY};

        return d;

    }

    public void updateLb() {

        if (lb1 != null) {
            lb1.setEndX(cnt1.getCenterX());
            lb1.setEndY(cnt1.getCenterY());

            lb2.setEndX(cnt2.getCenterX());
            lb2.setEndY(cnt2.getCenterY());
        }


    }


    public double getDistanceBetweenTwoPoints(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public double[] getArrowPoints(Line line, double length, double width) {
        double sx = line.getStartX(), ex = line.getEndX(), sy = line.getStartY(), ey = line.getEndY();
        double factor = length / Math.hypot(sx - ex, sy - ey);
        double factorO = width / Math.hypot(sx - ex, sy - ey);

        double dx = (sx - ex) * factor;
        double dy = (sy - ey) * factor;

        double ox = (sx - ex) * factorO;
        double oy = (sy - ey) * factorO;

        double d[] = {ex + dx - oy, ey + dy + ox, ex + dx + oy, ey + dy - ox};
        return d;
    }

    public void selectionMode(boolean selected) {
        if (selected) {
            cubicCurve.setStroke(Color.DARKGRAY);
            arrow.setStroke(Color.DARKGRAY);
            arrow.setFill(Color.DARKGRAY);
            selectedMode = true;
            probabilityLabel.setTextFill(Color.DARKGRAY);

        } else {
            cubicCurve.setStroke(Color.BLACK);
            arrow.setStroke(Color.BLACK);
            arrow.setFill(Color.BLACK);
            selectedMode = false;
            probabilityLabel.setTextFill(Color.BLACK);

        }
    }

    public void setArrow() {

        LineDistanceManipulator lineDistanceManipulator = new LineDistanceManipulator();
        Line line = lineDistanceManipulator.getLineAlteredFromEnd(cubicCurve.getStartX(), cubicCurve.getStartY(), cubicCurve.getEndX(), cubicCurve.getEndY(), 100);
        CubicCurve cv = new CubicCurve(cubicCurve.getStartX(), cubicCurve.getStartY(), cubicCurve.getControlX1(), cubicCurve.getControlY1(), cubicCurve.getControlX2(), cubicCurve.getControlY2(), line.getEndX(), line.getEndY());

        double middlePoint[] = getPointOnCurve(0.5, cv);


        line = lineDistanceManipulator.getLineAlteredFromBeginning(cubicCurve.getStartX(), cubicCurve.getStartY(), cubicCurve.getEndX(), cubicCurve.getEndY(), 300);
        cv = new CubicCurve(line.getStartX(), line.getStartY(), cubicCurve.getControlX1(), cubicCurve.getControlY1(), cubicCurve.getControlX2(), cubicCurve.getControlY2(), cubicCurve.getEndX(), cubicCurve.getEndY());


        double arrowHead[] = getPointOnCurve(0.5, cv);

        if (this.arrow == null) {
            arrow = new Polygon();
            c1 = new Circle();
            c2 = new Circle();
            c1.setStrokeWidth(5);
            c1.setFill(Color.RED);
            c2.setStrokeWidth(5);
            c1.setRadius(5);
            c2.setRadius(5);
            probabilityLabel = new Label();
            probabilityLabel.setFont(new Font("Times New Roman", 12));
            probabilityLabel.setTextFill(Color.BLACK);
            probabilityLabel.setText(".%");

            arrow.setStroke(Color.BLACK);
            arrow.setFill(Color.BLACK);

        }
        c1.setCenterX(middlePoint[0]);
        c1.setCenterY(middlePoint[1]);
        c2.setCenterX(arrowHead[0]);
        c2.setCenterY(arrowHead[1]);

        arrow.setStrokeWidth(2);

        double arrowLength = MainController.arrowLength;
        double arrowWidth = MainController.arrowWidth;


        if (cnt2 != null)
            line = lineDistanceManipulator.getLineAlteredFromEnd(cnt2.getCenterX(), cnt2.getCenterY(), cubicCurve.getEndX(), cubicCurve.getEndY(), 10);
        else
            line = lineDistanceManipulator.getLineAlteredFromEnd(cubicCurve.getStartX(), cubicCurve.getStartY(), cubicCurve.getEndX(), cubicCurve.getEndY(), 10);
        double sx = line.getEndX(), ex = cubicCurve.getEndX(), sy = line.getEndY(), ey = cubicCurve.getEndY();
        double factor = arrowLength / Math.hypot(sx - ex, sy - ey);
        double factorO = arrowWidth / Math.hypot(sx - ex, sy - ey);
        double dx = (sx - ex) * factor;
        double dy = (sy - ey) * factor;
        double ox = (sx - ex) * factorO;
        double oy = (sy - ey) * factorO;
        arrow.getPoints().removeAll(arrow.getPoints());
        arrow.getPoints().addAll(cubicCurve.getEndX(), cubicCurve.getEndY(), ex + dx - oy, ey + dy + ox, ex + dx + oy, ey + dy - ox);


        double c1x, c1y, c2x, c2y;
        double d[] = getPointOnCurve(0.6, cubicCurve);
        c1x = d[0];
        c1y = d[1];
        d = getPointOnCurve(0.7, cubicCurve);
        c2x = d[0];
        c2y = d[1];

        d = getArrowPoints(new Line(c1x, c1y, c2x, c2y), 5, 20);

        c1.setCenterX(d[0]);
        c1.setCenterY(d[1]);
        c2.setCenterX(d[2]);
        c2.setCenterY(d[3]);

        if (c1x <= c2x) {
            probabilityLabel.setLayoutX(d[0]);
            probabilityLabel.setLayoutY(d[1]);


        } else {
            probabilityLabel.setLayoutX(d[2]);
            probabilityLabel.setLayoutY(d[3]);

        }

        double f[] = getClosestCurvePoint(probabilityLabel.getLayoutX(), probabilityLabel.getLayoutY());
        if (getDistanceBetweenTwoPoints(f[0], f[1], getProbabilityLabel().getLayoutX() + 15, probabilityLabel.getLayoutY()) >= 1.5) {

            if (f[0] < probabilityLabel.getLayoutX())
                probabilityLabel.setLayoutX(probabilityLabel.getLayoutX() - (getDistanceBetweenTwoPoints(f[0], f[1], getProbabilityLabel().getLayoutX() + 15, probabilityLabel.getLayoutY()) - getDistanceBetweenTwoPoints(f[0], f[1], getProbabilityLabel().getLayoutX(), probabilityLabel.getLayoutY())));
            else
                probabilityLabel.setLayoutX(probabilityLabel.getLayoutX() - getProbabilityLabel().getText().length() * 3);


        }

    }

    public double[] getClosestCurvePoint(double x, double y) {
        double d[] = {0, 0};
        double min = 99999999;
        for (double i = 0; i < 100; i++) {
            double aux[] = getPointOnCurve(i / 100.0, cubicCurve);
            if (getDistanceBetweenTwoPoints(x, y, aux[0], aux[1]) < min) {
                min = getDistanceBetweenTwoPoints(x, y, aux[0], aux[1]);
                d[0] = aux[0];
                d[1] = aux[1];
            }

        }
        return d;
    }

    public static double calculateAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        angle = angle + Math.ceil(-angle / 360) * 360;

        return angle;
    }

    public void setArrowSelf(double cnt2x, double cnt2y) {
        double arrowLength = MainController.arrowLength;
        double arrowWidth = MainController.arrowWidth;
        LineDistanceManipulator lineDistanceManipulator = new LineDistanceManipulator();
        Line line = lineDistanceManipulator.getLineAlteredFromEnd(cnt2x, cnt2y, cubicCurve.getEndX(), cubicCurve.getEndY(), 10);
        double sx = line.getEndX(), ex = cubicCurve.getEndX(), sy = line.getEndY(), ey = cubicCurve.getEndY();
        double factor = arrowLength / Math.hypot(sx - ex, sy - ey);
        double factorO = arrowWidth / Math.hypot(sx - ex, sy - ey);
        double dx = (sx - ex) * factor;
        double dy = (sy - ey) * factor;
        double ox = (sx - ex) * factorO;
        double oy = (sy - ey) * factorO;
        arrow.getPoints().removeAll(arrow.getPoints());
        arrow.getPoints().addAll(cubicCurve.getEndX(), cubicCurve.getEndY(), ex + dx - oy, ey + dy + ox, ex + dx + oy, ey + dy - ox);

        if (probabilityLabel == null) {
            probabilityLabel = new Label();
            probabilityLabel.setFont(new Font("Impact", 12));
            probabilityLabel.setTextFill(Color.BLACK);
            probabilityLabel.setText(".34");
        }

        double c1x, c1y, c2x, c2y;
        double d[] = getPointOnCurve(0.6, cubicCurve);
        c1x = d[0];
        c1y = d[1];
        d = getPointOnCurve(0.7, cubicCurve);
        c2x = d[0];
        c2y = d[1];

        d = getArrowPoints(new Line(c1x, c1y, c2x, c2y), 5, 20);

        c1.setCenterX(d[0]);
        c1.setCenterY(d[1]);
        c2.setCenterX(d[2]);
        c2.setCenterY(d[3]);
        if (c1x < c2x) {
            probabilityLabel.setLayoutX(d[0] - 5);
            probabilityLabel.setLayoutY(d[1]);
        } else {
            probabilityLabel.setLayoutX(d[2] - 5);
            probabilityLabel.setLayoutY(d[3]);
        }


    }


    public CubicCurve getCubicCurve() {
        return cubicCurve;
    }

    public void setCubicCurve(CubicCurve cubicCurve) {
        this.cubicCurve = cubicCurve;
        setArrow();

        if (cnt1 == null)
            initCircles(this.cubicCurve);
        else
            changeCirclePosition();


        if (lb1 == null) {
            lb2.setStroke(null);
            lb2.setFill(null);

            lb1.setStroke(null);
            lb1.setFill(null);
        }


    }

    public void changeCirclePosition() {
        cnt1.setCenterX(cubicCurve.getControlX1());
        cnt1.setCenterY(cubicCurve.getControlY1());
        cnt2.setCenterX(cubicCurve.getControlX2());
        cnt2.setCenterY(cubicCurve.getControlY2());

    }

    public void initCircles(CubicCurve cubicCurve) {
        this.cnt1 = new Circle(cubicCurve.getControlX1(), cubicCurve.getControlY1(), 5);
        this.cnt2 = new Circle(cubicCurve.getControlX2(), cubicCurve.getControlY2(), 5);
        cnt1.setFill(Color.WHITE);
        cnt2.setFill(Color.WHITE);
        cnt1.setStrokeWidth(MainController.lineWidth);
        cnt2.setStrokeWidth(MainController.lineWidth);
        cnt1.setStroke(Color.BLACK);
        cnt2.setStroke(Color.BLACK);
        lb2 = new Line(0, 0, 0, 0);
        lb2.setStroke(null);
        lb2.setFill(null);

        lb1 = new Line(0, 0, 0, 0);
        lb1.setStroke(null);
        lb1.setFill(null);


    }

    public Circle getCnt1() {
        return cnt1;
    }

    public void setCnt1(Circle cnt1) {
        this.cnt1 = new Circle(cnt1.getCenterX(), cnt1.getCenterY(), 5);
    }

    public Circle getCnt2() {
        return cnt2;
    }

    public void setCnt2(Circle cnt2) {
        this.cnt2 = new Circle(cnt2.getCenterX(), cnt2.getCenterY(), 5);
    }


    public Polygon getArrow() {
        return arrow;
    }

    public void setArrow(Polygon arrow) {
        this.arrow = arrow;
    }

    public boolean isSelectedMode() {
        return selectedMode;
    }

    public void setSelectedMode(boolean selectedMode) {
        this.selectedMode = selectedMode;
    }
}
