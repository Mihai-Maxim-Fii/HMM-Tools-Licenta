package sample;

import java.io.Serializable;
import java.util.List;

public class SerializableTransition implements Serializable {
    private String from;
    private String to;
    private boolean isLineMode;
    private List<Point> curvePoints;
    private List<Point> dragPoints;
    private double probability;

    public SerializableTransition(String from, String to, List<Point> curvePoints, List<Point> dragPoints, double probability, boolean isLineMode) {
        this.isLineMode = isLineMode;
        this.from = from;
        this.to = to;
        this.curvePoints = curvePoints;
        this.dragPoints = dragPoints;
        this.probability = probability;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public List<Point> getCurvePoints() {
        return curvePoints;
    }

    public void setCurvePoints(List<Point> curvePoints) {
        this.curvePoints = curvePoints;
    }

    public List<Point> getDragPoints() {
        return dragPoints;
    }

    public void setDragPoints(List<Point> dragPoints) {
        this.dragPoints = dragPoints;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public boolean isLineMode() {
        return isLineMode;
    }

    public void setLineMode(boolean lineMode) {
        isLineMode = lineMode;
    }
}
