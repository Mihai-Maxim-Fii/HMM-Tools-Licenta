package sample;


import javafx.scene.control.Label;

public class StepProbability {
    private Label label;
    private double stepProbability = -1;
    private State state;


    public StepProbability(Label label, State state) {
        this.label = label;
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public double getStepProbability() {
        return stepProbability;
    }

    public void setStepProbability(double stepProbability) {
        this.stepProbability = stepProbability;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }
}
