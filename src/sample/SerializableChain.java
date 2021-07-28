package sample;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SerializableChain implements Serializable {

    private List<SerializableState> states = new ArrayList<>();
    private List<SerializableObservation> observations = new ArrayList<>();
    private List<SerializableTransition> transitions = new ArrayList<>();
    private double screenWidth;
    private double screenHeight;

    public List<SerializableState> getStates() {
        return states;
    }

    public void setStates(List<SerializableState> states) {
        this.states = states;
    }

    public List<SerializableObservation> getObservations() {
        return observations;
    }

    public void setObservations(List<SerializableObservation> observations) {
        this.observations = observations;
    }

    public List<SerializableTransition> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<SerializableTransition> transitions) {
        this.transitions = transitions;
    }

    public double getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(double screenWidth) {
        this.screenWidth = screenWidth;
    }

    public double getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(double screenHeight) {
        this.screenHeight = screenHeight;
    }
}
