/**
 * Implements an interface for the alpha-delta algorithm that relies on console input.
 *
 * @author nkullman
 * @version %I%, %G%
 * @since Apr 11, 2016
 */

package mco.alphadelta.implementation;

import mco.alphadelta.framework.IADAlgoInterface;
import mco.alphadelta.framework.IADAlgoParameters;
import mco.alphadelta.framework.IADAlgoSolver;
import mco.alphadelta.framework.IADSolverParameters;

import java.io.File;

public class ADAlgoConsoleInterface implements IADAlgoInterface {

    IADAlgoParameters algoParams = null;
    IADSolverParameters solverParams = null;
    File mcoModel = null;
    IADAlgoSolver algoSolver = null;

    public IADAlgoParameters getADAlgorithmParameters() {
        return null;
    }

    public void setADAlgorithmParameters(IADAlgoParameters algoParameters) {
        this.algoParams = algoParameters;
    }

    public IADSolverParameters getADSolverParameters() {
        return null;
    }

    public void setADSolverParameters(IADSolverParameters solverParameters) {
        this.solverParams = solverParameters;
    }

    public File getMCOModel() {
        return null;
    }

    public void setMCOModel(File mcoModel) {
        this.mcoModel = mcoModel;
    }

    public void setADSolver(IADAlgoSolver algoSolver) {
        this.algoSolver = algoSolver;
    }

    public IADAlgoSolver getADSolver() {
        return null;
    }

    public boolean runSolver(IADAlgoSolver solver) {
        if (this.algoSolver == null) {
            System.out.println("Error: No solver assigned. Please first assign a solver, then try again.");
            return false;
        }
        return false;
    }

    public void prepAlgorithm() {
        setADSolver(getADSolver());
        setADAlgorithmParameters(getADAlgorithmParameters());
        setADSolverParameters(getADSolverParameters());
        setMCOModel(getMCOModel());
    }
}
