package sample;

import java.util.ArrayList;
import java.util.List;

public class Chain {
    private List<State> states;
    private List<Observation> observations;
    private double transitionMatrix[][];

    public Chain() {
        states = new ArrayList<>();
        observations = new ArrayList<>();
    }

    public Chain(List<State> states, List<Observation> observations) {
        this.states = states;
        this.observations = observations;

    }

    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }

    public double[][] getTransitionMatrix() {
        return transitionMatrix;
    }

    public void setTransitionMatrix(double[][] transitionMatrix) {
        this.transitionMatrix = transitionMatrix;
    }

    public List<Observation> getObservations() {
        return observations;
    }

    public void setObservations(List<Observation> observations) {
        this.observations = observations;
    }
}
