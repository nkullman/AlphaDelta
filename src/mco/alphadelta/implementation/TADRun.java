package mco.alphadelta.implementation;

import mco.alphadelta.framework.IADAlgoInterface;

/**
 * Test class for implementing the alpha-delta algorithm.
 *
 * @author nkullman
 * @version %I%, %G%
 * @since Apr 12, 2016
 */
public class TADRun {

    public static void main(String[] args) {
        // instantiate new interface
        IADAlgoInterface algoInterface = new ADAlgoConsoleInterface();
        // set the parameters, model, and solver
        algoInterface.prepAlgorithm();
        // run
        algoInterface.runSolver();
    }
}
