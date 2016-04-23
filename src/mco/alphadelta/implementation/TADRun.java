package mco.alphadelta.implementation;

import ilog.concert.IloException;

import java.io.File;

/**
 * Test class for implementing the alpha-delta algorithm.
 *
 * @author nkullman
 * @version %I%, %G%
 * @since Apr 12, 2016
 */
public class TADRun {

    public static void main(String[] args) throws IloException {
        // instantiate new interface
        //IADAlgoInterface algoInterface = new ADAlgoConsoleInterface();
        // set the parameters, model, and solver
        //algoInterface.prepAlgorithm();
        // run
        //algoInterface.runSolver();

        ADAlgoSolver_CPLEX test = new ADAlgoSolver_CPLEX();
        test.solve(new File("C:\\Users\\nkullman\\Desktop\\AlphaDeltaDesktopItems\\SendToEuclidForADP\\lastModelFile.lp"));
    }
}
