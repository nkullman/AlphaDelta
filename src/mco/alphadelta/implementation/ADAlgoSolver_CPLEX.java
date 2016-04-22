package mco.alphadelta.implementation;

import mco.alphadelta.framework.IADAlgoParameters;
import mco.alphadelta.framework.IADAlgoSolver;
import mco.alphadelta.framework.IADSolverParameters;

import java.io.File;

/**
 * Created by Nick on 4/22/2016.
 */
public class ADAlgoSolver_CPLEX extends cplex implements IADAlgoSolver {

    @Override
    public boolean solve(File mcoModel) {
        return false;
    }

    @Override
    public boolean setSolverParameters(IADSolverParameters solverParameters) {
        return false;
    }

    @Override
    public boolean setAlgoParameters(IADAlgoParameters algoParameters) {
        return false;
    }

}
