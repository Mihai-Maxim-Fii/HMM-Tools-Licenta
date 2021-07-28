package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import sample.State;
import sample.StepProbability;

import java.math.BigDecimal;

public class TableViewController {
    @FXML
    private TextArea textBox;
    private BigDecimal[][] probabilityMatrix;
    private int mode;
    private SimulatorController simulatorController;
    private String[] query;
    String infoText = "";
    String showText = "";

    public void setTable(BigDecimal[][] probabilityMatrix, String[] query, int mode, SimulatorController simulatorController) {
        this.probabilityMatrix = probabilityMatrix;
        this.mode = mode;
        this.simulatorController = simulatorController;
        this.query = query;


        showText += makeTable();

        if (mode == 0) {
            BigDecimal sum = BigDecimal.ZERO;
            for (int i = 0; i < simulatorController.getSimulatorChain().size(); i++) {
                sum = sum.add(probabilityMatrix[i][query.length - 1]);
            }
            infoText += "The estimated probability is:" + sum.toString();

        }

        if (mode == 1) {
            String text = "";
            for (int i = 0; i < query.length; i++) {
                BigDecimal max = BigDecimal.ZERO;
                int x = 0;
                for (int j = 0; j < simulatorController.getSimulatorChain().size(); j++) {
                    if (probabilityMatrix[j][i].compareTo(max) == 1) {
                        x = j;
                        max = probabilityMatrix[j][i];
                    }
                }
                text += simulatorController.getIncidenceMatrixChain().getStateOrderTree().get(x) + " ";
            }
            infoText += "The most probable sequence is :\n" + text;


        }
        textBox.setText(showText + infoText);

    }

    public void setTable( String[] query, SimulatorController simulatorController) {
        this.simulatorController = simulatorController;
        this.query = query;
        BigDecimal probability = BigDecimal.ZERO;
        int i = 0;
        BigDecimal stp = BigDecimal.ZERO;
        for (StepProbability stepProbability : simulatorController.stepProbabilities) {
            if (stepProbability.getState().getStateName().getText().compareTo(query[0]) == 0) {
                stp = BigDecimal.valueOf(stepProbability.getStepProbability());
            }

        }
        String lastTrans = "";
        for (String str : query) {
            if (i == 0) {
                probability = stp;
            } else {
                int l = simulatorController.getIncidenceMatrixChain().getStateOrder().get(lastTrans);
                int c = simulatorController.getIncidenceMatrixChain().getStateOrder().get(str);
                BigDecimal p = BigDecimal.valueOf(simulatorController.getIncidenceMatrixChain().getIncidenceMatrix()[l][c]);

                probability = probability.multiply(p);
            }
            lastTrans = str;
            i+=1;
        }
          infoText="The probability of the state sequence ";
            for(String str:query)
            {
                infoText+=str+",";
            }
            infoText=infoText.substring(0,infoText.length()-1);
            infoText+=" is :"+probability.toString();
            textBox.setText(infoText);

    }


    public String padRight(String s, int n) {
        for (int i = 0; i < n; i++) {
            s += " ";
        }
        return s;
    }

    public String padLeft(String s, int n) {
        String aux = "";
        for (int i = 0; i < n; i++) {
            aux += " ";
        }
        aux += s;
        return aux;
    }


    public String makeTable() {
        int max = 10;
        int max1 = 0, max2 = 0;
        for (String s : query) {
            if (s.length() > max2)
                max2 = s.length();
        }
        for (State s : simulatorController.getSimulatorChain()) {
            if (s.getStateName().getText().length() > max1)
                max1 = s.getStateName().getText().length();
        }
        for (int i = 0; i < simulatorController.getSimulatorChain().size(); i++) {
            for (int j = 0; j < query.length; j++) {
                if (probabilityMatrix[i][j].toString().length() > max2) {
                    max2 = probabilityMatrix[i][j].toString().length();
                }
            }
        }
        String table = "";

        table = padLeft(table, max1);
        table = padRight(table, 1);

        for (String s : query) {
            table += s;
            if (s.length() < max2) {
                table = padRight(table, max2 - s.length());

            }
            table = padRight(table, 1);

        }
        table += "\n";

        for (int i = 0; i < simulatorController.getSimulatorChain().size(); i++) {
            String s = simulatorController.getIncidenceMatrixChain().getStateOrderTree().get(i);
            table += s;
            if (s.length() < max1) {
                table = padRight(table, max1 - s.length());
            }
            table = padRight(table, 1);
            for (int j = 0; j < query.length; j++) {
                s = probabilityMatrix[i][j].toString();

                table += s;
                if (s.length() < max2) {
                    table = padRight(table, max2 - s.length());
                }
                table += " ";
            }
            table += "\n";
        }
        return table;
    }

}
