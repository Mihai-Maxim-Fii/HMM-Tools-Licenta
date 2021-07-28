package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;

public class Transition {
    private State from;
    private State to;
    private Observation obsTo;
    private double probability;
    private Line line;
    private CubicCurve cubicCurve;
    private Circle cnt1, cnt2;
    private Curve curve;


    public Curve getCurve() {
        return curve;
    }

    public void setCurve(Curve curve) {
        this.curve = curve;
    }


    public Transition(State from, State to, double probability, Line line) {
        this.from = from;
        this.to = to;
        this.probability = probability;
        this.line = line;
        CubicCurveCreator cubicCurveCreator = new CubicCurveCreator();
        CubicCurve c2 = cubicCurveCreator.createCubicCurveFromLine(this.line);
        this.cubicCurve = c2;
        initCircles(c2);
        this.curve = new Curve();
        this.curve.setCubicCurve(this.cubicCurve);


    }

    public Transition(State from, Observation to, double probability, Line line) {
        this.from = from;
        this.obsTo = to;
        this.probability = probability;
        this.line = line;
        CubicCurveCreator cubicCurveCreator = new CubicCurveCreator();
        CubicCurve c2 = cubicCurveCreator.createCubicCurveFromLine(this.line);
        this.cubicCurve = c2;
        initCircles(c2);
        this.curve = new Curve();
        this.curve.setCubicCurve(this.cubicCurve);

    }

    public void initCircles(CubicCurve cubicCurve) {
        this.cnt1 = new Circle(cubicCurve.getControlX1(), cubicCurve.getControlY1(), 5);
        this.cnt2 = new Circle(cubicCurve.getControlX2(), cubicCurve.getControlY2(), 5);
        cnt1.setFill(Color.WHITE);
        cnt2.setFill(Color.WHITE);
        cnt1.setStrokeWidth(4);
        cnt2.setStrokeWidth(4);


    }

    public State getFrom() {
        return from;
    }

    public void setFrom(State from) {
        this.from = from;
    }

    public State getTo() {
        return to;
    }

    public void setTo(State to) {
        this.to = to;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public CubicCurve getCubicCurve() {
        return cubicCurve;
    }

    public void setCubicCurve(CubicCurve cubicCurve) {
        this.cubicCurve.setStartX(cubicCurve.getStartX());
        this.cubicCurve.setStartY(cubicCurve.getStartY());

        this.cubicCurve.setControlX1(cubicCurve.getControlX1());
        this.cubicCurve.setControlY1(cubicCurve.getControlY1());

        this.cubicCurve.setControlX2(cubicCurve.getControlX2());
        this.cubicCurve.setControlY2(cubicCurve.getControlY2());

        this.cubicCurve.setEndX(cubicCurve.getEndX());
        this.cubicCurve.setEndY(cubicCurve.getEndY());


        this.curve.setCubicCurve(this.cubicCurve);
        changeCirclePosition();
    }

    public void changeCirclePosition() {
        cnt1.setCenterX(cubicCurve.getControlX1());
        cnt1.setCenterY(cubicCurve.getControlY1());
        cnt2.setCenterX(cubicCurve.getControlX2());
        cnt2.setCenterY(cubicCurve.getControlY2());
    }

    public Circle getCnt1() {
        return cnt1;
    }

    public void setCnt1(Circle cnt1) {
        this.cnt1 = cnt1;
    }

    public Circle getCnt2() {
        return cnt2;
    }

    public void setCnt2(Circle cnt2) {
        this.cnt2 = cnt2;
    }

    public Observation getObsTo() {
        return obsTo;
    }

    public void setObsTo(Observation obsTo) {
        this.obsTo = obsTo;
    }
}
