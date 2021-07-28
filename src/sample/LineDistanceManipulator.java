package sample;


import controllers.MainController;
import javafx.scene.shape.Line;

public class LineDistanceManipulator {


    public LineDistanceManipulator() {

    }


    public Line getLineAlteredFromBeginning(double startX, double startY, double endX, double endY, double d2) {
        double distance = getDistance(startX, startY, endX, endY);

        d2 = distance - d2;
        double Xc, Yc;
        Xc = endX - (d2 * (endX - startX)) / distance;
        Yc = endY - (d2 * (endY - startY)) / distance;



        Line l = new Line(Xc, Yc, endX, endY);
        return l;

    }

    public Line getLineAlteredFromEnd(double startX, double startY, double endX, double endY, double d2) {
        double distance = getDistance(startX, startY, endX, endY);

        d2 = distance - d2;
        double Xc, Yc;
        Xc = startX - (d2 * (startX - endX)) / distance;
        Yc = startY - (d2 * (startY - endY)) / distance;


        Line l = new Line(startX, startY, Xc, Yc);
        return l;
    }

    public double getDistanceToSquareBorder(double ox, double oy, double px, double py) {
        double Dx = Math.abs(px - ox);
        double Dy = Math.abs(py - oy);
        double tan = Math.min(Dx, Dy) / Math.max(Dx, Dy);
        double squaredCos = 1.0 / (1.0 + Math.pow(tan, 2));
        double cos = Math.sqrt(squaredCos);
        return ((MainController.observationSide + MainController.lineWidth) / 2) / (cos);

    }

    public double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }


}
