package mco.alphadelta.implementation;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import mco.alphadelta.framework.IADAlgoParameters;
import mco.alphadelta.framework.IADAlgoSolver;
import mco.alphadelta.framework.IADSolverParameters;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by Nick on 4/22/2016.
 */
public class ADAlgoSolver_CPLEX extends ilog.cplex.IloCplex implements IADAlgoSolver {

    private IloCplex cplex;
    private File originalModelFile = null;
    private File lastModelFile = null;
    private File outputPath = null;
    private String timeOutputPathSpecified = null;
    private IADAlgoParameters algoParameters = null;
    private ADSolverCPLEXParameters cplexParameters = null;
    private ArrayList<String> objectives = null;
    private Map<String, Integer> objectiveSenses = null;

    public ADAlgoSolver_CPLEX() throws IloException {
        this.cplex = new IloCplex();
    }

    @Override
    public boolean setSolverParameters(IADSolverParameters solverParameters) {
        this.cplexParameters = (ADSolverCPLEXParameters) solverParameters;
        return true;
    }

    @Override
    public boolean setAlgoParameters(IADAlgoParameters algoParameters) {
        this.algoParameters = algoParameters;
        return true;
    }

    @Override
    public boolean setOutputPath(File outputPath) {
        // create a subdirectory of outputPath to store output
        // subdirectory's name is a timestamp to ensure uniqueness
        timeOutputPathSpecified = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(Calendar.getInstance().getTime());

        // create directory
        File outDir = new File(outputPath.getAbsolutePath() + "/alphadelta_" + timeOutputPathSpecified);
        System.out.println(outDir.toString());
        outDir.mkdir();

        // set that folder to our output folder
        this.outputPath = outDir;
        return true;
    }

    @Override
    public String getType() {
        return "cplex";
    }

    @Override
    public boolean solve(File mcoModel) throws IloException {

        this.originalModelFile = mcoModel;
        this.lastModelFile = mcoModel;

        cplex.importModel(mcoModel.getAbsolutePath());

        // get objectives

        // set objectives' senses

        //


        //return cplex.solve();
        return false;
    }

}

// TODO add functionality for hot start
// requires the hot start model and extracting ideal and worst case values
// TODO put single-obj solutions in primary output file (CSV)
// TODO error handling for a bad model file
// TODO parsing of objectives from model file - use... token-based scanner?
// TODO error handling for bad CPLEX param values
// TODO error handling for bad algo param values
// TODO error handling for objectives not in conflict

