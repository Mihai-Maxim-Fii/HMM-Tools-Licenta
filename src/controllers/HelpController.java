package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class HelpController {
    @FXML
    private TextArea textBox;

    public void setText(int textNumber) {
        String toShow = "";
        if (textNumber == 2) {
            toShow = "To enter simulator mode you must:\n" +
                    "1)Build a valid HMM(Hidden Markov Model) or Markov Chain.\n" +
                    "2)Select it while being in validation mode (the black arrow button).\n" +
                    "3)Right click on a state from the HMM/Markov Chain and select \"Send To Simulator\" .\n" +
                    "\n" +
                    "If a HMM/Markov Chain is valid ,all it's elements will be highlighted in green after selection.\n" +
                    "Elements highlighted in red point you to the things that need to be changed in order to obtain a valid structure.\n" +
                    "Examples:If (only) the state/observation name is red it means that there are elements within the structure that share the same name.\n" +
                    "If the transitions from a Markov chain are red it means that some probabilities are not adding up to 1.\n" +
                    "If a transition towards an observation and the observation are red it means that not all states from the Markov Chain are connected to that observation.\n" +
                    "\n" +
                    "Once you entered simulator mode you can run multiple HMM algorithms(if the structure is a HMM),move through the Markov Chain using the transition matrix,get the stationary distribution.\n" +
                    "In order to be able to use most of the algorithms , you must set a starting probability distribution for the Markov Chain." +
                    "To do that enter selection mode(the cursor button) and click on the \"NaN\" symbols.\n" +
                    "To use an algorithm select it from the \"Query\" tab.\n" +
                    "In order to enter Training mode select Training from Query and provide the requested data:Observation sequence sepparated by comma ,ex:O1,O1,O2,O2,O1\n" +
                    "In order to reset all the probabilities click on the round button.\n" +
                    "You can get all the probabilities of the model at any point in time in text form by clicking on File->Get Model data.\n" +
                    "You can also take a snapshot of the model by clicking on File->Take snapshot\n";
        } else if (textNumber == 1) {
            toShow = "States are represented by circles,Observations by squares.\n" +
                    "States and Observations can be drawn by pressing the circle/square buttons and clicking on the drawing pane(White board).\n" +
                    "\n" +
                    "Enter transition drawing mode by pressing the arrow button(-->).\n" +
                    "There are two transition types: State->State,State->Observation.\n" +
                    "Transitions are drawn by clicking on an empty space within a state and then dragging the mouse to another state/observation." +
                    "(you should see a line extending as you drag the mouse).\n" +
                    "To change the name of a state/observation enter state/observation drawing mode then click on the name of the state/observation that you wish to modify.\n" +
                    "To set the probability of a transition make sure that you are in transition drawing mode and click on the % symbol appearing near the transition.\n" +
                    "\n" +
                    "The positions of the elements drawn on the screen can be modified by entering selection mode.\n" +
                    "To enter selection mode click on the cursor button.\n" +
                    "You can modify the shape of a transition by clicking on it while being in selection mode.\n" +
                    "You can draw a self transitions to a state by selecting it and then clicking on the circular arrow button.\n" +
                    "Transitions cand be straightened(returned to their original shape) by selecting the transition(in selection mode) and then clicking on the arrow button(-->).\n" +
                    "Selected elements are highlighted in grey.\n" +
                    "You can remove elements by selecting them and clicking on the thrash bin button.\n" +
                    "Multiple elements can be moved at once in a similar matter by entering validation mode(clicking on the black cursor) and dragging states.\n" +
                    "To expand the drawing pane click on the multiple arrow button.";
        } else {
            toShow = "Select zoom level by clicking on \"Zoom\"\n" +
                    "Save a project by clicking on File ->Save Project\n" +
                    "Open a previous project by clicking on File->Open Project\n" +
                    "Clear the drawing board by clicking on File->New->Drawing board\n" +
                    "Draw a Markov Chain from a probability matrix by clicking on File->New->Chain from probability matrix\n" +
                    "Matrix format:numbers separated by spaces=>\n" +
                    "ex: 0.5 0   0.5\n" +
                    "    1   0   0\n" +
                    "    0.2 0.2 0.6\n" +
                    "State names format:names followed by comma, ex:A,B,C";
        }
        textBox.setEditable(false);
        textBox.setText(toShow);


    }
}
