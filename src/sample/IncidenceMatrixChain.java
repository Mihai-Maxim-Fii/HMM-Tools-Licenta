package sample;

import java.util.HashMap;
import java.util.TreeMap;

public class IncidenceMatrixChain {

    private HashMap<String, Integer> stateOrder;
    private TreeMap<Integer, String> stateOrderTree;
    private int stateCount;
    private double[][] incidenceMatrix;

    public IncidenceMatrixChain(HashMap<String, Integer> stateOrder, TreeMap<Integer, String> stateOrderTree, int stateCount, double[][] incidenceMatrix) {
        this.stateOrder = stateOrder;
        this.stateOrderTree = stateOrderTree;
        this.stateCount = stateCount;
        this.incidenceMatrix = incidenceMatrix;
    }

    public HashMap<String, Integer> getStateOrder() {
        return stateOrder;
    }

    public void setStateOrder(HashMap<String, Integer> stateOrder) {
        this.stateOrder = stateOrder;
    }

    public TreeMap<Integer, String> getStateOrderTree() {
        return stateOrderTree;
    }

    public void setStateOrderTree(TreeMap<Integer, String> stateOrderTree) {
        this.stateOrderTree = stateOrderTree;
    }

    public int getStateCount() {
        return stateCount;
    }

    public void setStateCount(int stateCount) {
        this.stateCount = stateCount;
    }

    public double[][] getIncidenceMatrix() {
        return incidenceMatrix;
    }

    public void setIncidenceMatrix(double[][] incidenceMatrix) {
        this.incidenceMatrix = incidenceMatrix;
    }
}
