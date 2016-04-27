/**
 * Defines the interface for a solver for the alpha-delta algorithm.
 *
 * @author nkullman
 * @version %I%, %G%
 * @since Apr 11, 2016
 */
package mco.alphadelta.framework;

import ilog.concert.IloException;

import java.io.File;
import java.io.FileNotFoundException;

public interface IADAlgoSolver {
    /**
     * Solves the multi-criterion optimization model by executing the
     * alpha-delta algorithm.
     *
     * @return true if the algorithm terminates successfully
     */
    boolean solve(File mcoModel) throws IloException, FileNotFoundException;

    /**
     * Set the parameters of the solver using the IADSolverParameters object passed.
     *
     * @param solverParameters the params to to configure the solver
     * @return true if the parameters are set successfully.
     */
    boolean setSolverParameters(IADSolverParameters solverParameters);

    /**
     * Set the parameters for the alpha-delta algorithm that the solver will use during its execution
     * @param algoParameters
     * @return
     */
    boolean setAlgoParameters(IADAlgoParameters algoParameters);

    /**
     * Sets the location where the solver will store output generated during the execution of the alpha-delta algorithm
     *
     * @param outputPath
     * @return
     */
    boolean setOutputPath(File outputPath);

    /**
     * Returns the type of this.solver, such as "CPLEX"
     *
     * @return
     */
    String getType();
}
