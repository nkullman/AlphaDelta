/**
 * Defines the interface for alpha-delta algorithm interfaces.
 *
 * @author nkullman
 * @version %I%, %G%
 * @since Apr 11, 2016
 */

package mco.alphadelta.framework;

import java.io.File;

public interface IADAlgoInterface {

    /**
     * Gets the alpha-delta parameters from the user
     *
     * @return the user-passed parameter set
     */
    IADAlgoParameters getADAlgorithmParameters();

    /**
     * Assigns the argument to be the parameter set for the alpha-delta algorithm
     *
     * @param algoParameters
     */
    void setADAlgorithmParameters(IADAlgoParameters algoParameters);

    /**
     * Gets and returns solver parameters from the user
     *
     * @return
     */
    IADSolverParameters getADSolverParameters();

    /**
     * Assigns the argument to be the parameter set for the solver that executes the alpha-delta algorithm
     *
     * @param solverParameters
     */
    void setADSolverParameters(IADSolverParameters solverParameters);

    /**
     * Gets and returns the user's file containing the multi-criterion optimization model
     *
     * @return
     */
    File getMCOModel();

    /**
     * Assigns the argument as the multi-criterion optimization model to be solved
     *
     * @param mcoModel
     */
    void setMCOModel(File mcoModel);

    /**
     * Assigns the argument to be the solver that executes the alpha-delta algorithm.
     *
     * @param algoSolver
     */
    void setADSolver(IADAlgoSolver algoSolver);

    /**
     * Uses this.solver to execute the alpha-delta algorithm
     *
     * @return true if the solver executes successfully
     */
    boolean runSolver();

    /**
     * Prepares the algorithm for execution. This may include setting the solver, algorithmic parameters, solver parameters, etc.
     */
    void prepAlgorithm();

    /**
     * Gets and returns the user's desired solver for the alpha-delta algorithm
     *
     * @return
     */
    IADAlgoSolver getADSolver();
}