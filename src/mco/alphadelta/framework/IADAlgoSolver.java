/**
 * Defines the interface for a solver for the alpha-delta algorithm.
 *
 * @author nkullman
 * @version %I%, %G%
 * @since Apr 11, 2016
 */
package mco.alphadelta.framework;

import java.io.File;

public interface IADAlgoSolver {
    /**
     * Solves the multi-criterion optimization model by executing the
     * alpha-delta algorithm.
     *
     * @return true if the algorithm terminates successfully
     */
    public boolean solve(File mcoModel);

    /**
     * Set the parameters of the solver using the IADSolverParameters object passed.
     *
     * @param solverParameters the params to to configure the solver
     * @return true if the parameters are set successfully.
     */
    public boolean setSolverParameters(IADSolverParameters solverParameters);

    /**
     * Set the parameters for the alpha-delta algorithm that the solver will use during its execution
     * @param algoParameters
     * @return
     */
    public boolean setAlgoParameters(IADAlgoParameters algoParameters);
}
