package mco.alphadelta.implementation;

import ilog.concert.*;
import ilog.cplex.IloCplex;
import mco.alphadelta.framework.IADAlgoParameters;
import mco.alphadelta.framework.IADAlgoSolver;
import mco.alphadelta.framework.IADSolverParameters;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nick on 4/22/2016.
 */
public class ADAlgoSolver_CPLEX extends ilog.cplex.IloCplex implements IADAlgoSolver {

    private IloCplex cplex = null;
    private File originalModelFile = null;
    private File lastModelFile = null;
    private File outputPath = null;
    private String timeOutputPathSpecified = null;
    private ADAlgoParameters algoParameters = null;
    private ADSolverCPLEXParameters cplexParameters = null;
    private ArrayList<String> objectives = null;
    private int numObjectives = -1;
    private Map<String, Integer> objectiveSenses = null;
    private Map<String, Integer> objectiveColumns = null;
    private Map<String, Double> objectiveIdeals = null;
    private Map<String, Double> objectiveNadirs = null;

    public ADAlgoSolver_CPLEX() throws IloException {

        this.cplex = new IloCplex();

        this.objectives = new ArrayList<>();
        this.objectiveSenses = new HashMap<>();
        this.objectiveColumns = new HashMap<>();
        this.objectiveIdeals = new HashMap<>();
        this.objectiveNadirs = new HashMap<>();

        this.outputPath = new File(System.getProperty("user.home"));
        this.timeOutputPathSpecified = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(Calendar.getInstance().getTime());
    }

    @Override
    public boolean setSolverParameters(IADSolverParameters solverParameters) {

        // store the passed params as our own
        this.cplexParameters = (ADSolverCPLEXParameters) solverParameters;

        // set the new parameters in CPLEX
        assignSolverParameters(this.cplex, this.cplexParameters);

        return true;
    }

    private void assignSolverParameters(IloCplex cplex, ADSolverCPLEXParameters cplexParameters) {

        try {
            // clear any existing parameters
            cplex.getParameterSet().clear();

            // assign new ones
            for (String paramName : cplexParameters.getParamSet()) {
                switch (paramName.toLowerCase()) {
                    case "threads":
                        cplex.setParam(IntParam.Threads, Integer.parseInt(this.cplexParameters.getParam(paramName)));
                        break;
                    case "numthreads":
                        cplex.setParam(IntParam.Threads, Integer.parseInt(this.cplexParameters.getParam(paramName)));
                        break;
                    case "epgap":
                        cplex.setParam(DoubleParam.EpGap, Double.parseDouble(this.cplexParameters.getParam(paramName)));
                        break;
                    case "optgap":
                        cplex.setParam(DoubleParam.EpGap, Double.parseDouble(this.cplexParameters.getParam(paramName)));
                        break;
                    case "optimalitygap":
                        cplex.setParam(DoubleParam.EpGap, Double.parseDouble(this.cplexParameters.getParam(paramName)));
                        break;
                    case "workmem":
                        cplex.setParam(DoubleParam.WorkMem, Double.parseDouble(this.cplexParameters.getParam(paramName)));
                        break;
                    case "workingmem":
                        cplex.setParam(DoubleParam.WorkMem, Double.parseDouble(this.cplexParameters.getParam(paramName)));
                        break;
                    case "workingmemory":
                        cplex.setParam(DoubleParam.WorkMem, Double.parseDouble(this.cplexParameters.getParam(paramName)));
                        break;
                    case "memory":
                        cplex.setParam(DoubleParam.WorkMem, Double.parseDouble(this.cplexParameters.getParam(paramName)));
                        break;
                    case "epint":
                        cplex.setParam(DoubleParam.EpInt, Double.parseDouble(this.cplexParameters.getParam(paramName)));
                        break;
                    case "integrality":
                        cplex.setParam(DoubleParam.EpInt, Double.parseDouble(this.cplexParameters.getParam(paramName)));
                        break;
                    case "intgap":
                        cplex.setParam(DoubleParam.EpInt, Double.parseDouble(this.cplexParameters.getParam(paramName)));
                        break;
                    case "integralitygap":
                        cplex.setParam(DoubleParam.EpInt, Double.parseDouble(this.cplexParameters.getParam(paramName)));
                        break;
                    case "tilim":
                        cplex.setParam(DoubleParam.TiLim, Double.parseDouble(this.cplexParameters.getParam(paramName)));
                        break;
                    case "time":
                        cplex.setParam(DoubleParam.TiLim, Double.parseDouble(this.cplexParameters.getParam(paramName)));
                        break;
                    case "timelimit":
                        cplex.setParam(DoubleParam.TiLim, Double.parseDouble(this.cplexParameters.getParam(paramName)));
                        break;
                    default:
                        System.out.println("Unrecognized CPLEX parameter: " + paramName);
                        System.out.println("No value set for this parameter.");
                        break;
                }
            }
        } catch (IloException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean setAlgoParameters(IADAlgoParameters algoParameters) {
        this.algoParameters = (ADAlgoParameters) algoParameters;
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
        // the below procedure will need to be altered in the event of a hot start

        this.originalModelFile = mcoModel;
        this.lastModelFile = mcoModel;

        cplex.importModel(mcoModel.getAbsolutePath());

        // record the objectives and their senses (max or min)
        getObjsAndSenses(cplex.getObjective());

        // solve for single-objective bests
        for (String objective : this.objectives)
            getSingleObjectiveBests(cplex, objective);

        // improve other objs single-objective bests (reqs all three bests to be known so that proper obj fn weights may be used)

        // define ideal solution

        // define nadir solution

        // calculate relative deltas


        //return cplex.solve();
        return false;
    }

    private boolean getSingleObjectiveBests(IloCplex cplex, String objective) {
        // temporarily reset the solver parameters to those specific to the construction of the ideal solution
        if (this.algoParameters != null && this.algoParameters.getSolverParamsForIdealConstruction() != null)
            assignSolverParameters(this.cplex,
                    (ADSolverCPLEXParameters) this.algoParameters.getSolverParamsForIdealConstruction());

        try {
            // construct single-objective objective function
            IloLinearNumExpr newObj = cplex.linearNumExpr();
            newObj.addTerm(1, ((IloLPMatrix) cplex.LPMatrixIterator().next()).getNumVar(objectiveColumns.get(objective)));
            // remove previous objective function
            cplex.remove(cplex.getObjective());
            // reassign the new one
            cplex.add(cplex.objective((objectiveSenses.get(objective) == 0) ? IloObjectiveSense.Minimize : IloObjectiveSense.Maximize, newObj, "objective"));
            // solve the LP
            if (cplex.solve()) {
                // store the ideal value for this objective
                this.objectiveIdeals.put(objective, cplex.getObjValue());
            }
        } catch (IloException e) {
            e.printStackTrace();
            return false;
        }

        // return the solver parameters to their original values
        if (this.cplexParameters != null)
            assignSolverParameters(this.cplex, this.cplexParameters);

        return true;
    }

    private void getObjsAndSenses(IloObjective objective) {
        try {
            // Overall sense of objective function. 1 for max, 0 for min
            int overallSense = (objective.getSense() == IloObjectiveSense.Maximize) ? 1 : 0;

            // store objective function's numerical expression as string
            String objString = objective.getExpr().toString();

            String termRegex = "((-|\\+)? |-)?[-.0-9E]+\\*\\w+";
            String zeroCoefficientRegex = "\\D0.0\\D";

            Pattern checkTermRegex = Pattern.compile(termRegex);
            Pattern checkZeroRegex = Pattern.compile(zeroCoefficientRegex);

            Matcher termRegexMatcher = checkTermRegex.matcher(objString);
            Matcher zeroRegexMatcher;
            String newTerm;
            while (termRegexMatcher.find()) {
                newTerm = termRegexMatcher.group().trim();
                zeroRegexMatcher = checkZeroRegex.matcher(newTerm);
                if (zeroRegexMatcher.find() && zeroRegexMatcher.group().trim().length() > 0)
                    break;

                String[] termParts = newTerm.split("\\*", 2);

                // clean strings to be parsed as doubles
                if (termParts[0].startsWith("+")) termParts[0] = termParts[0].substring(1).trim();
                if (termParts[0].startsWith("-")) termParts[0] = termParts[0].replace(" ", "");

                // Determine if the coefficient is negative (0) or positive (1)
                int coefficientSign = (Double.valueOf(termParts[0]) > 0) ? 1 : 0;
                // and store the objective's name
                String objName = termParts[1];
                this.objectives.add(objName);
                // and its column in the CPLEX LP matrix
                this.objectiveColumns.put(objName, this.objectives.indexOf(objName));

                // If the coefficient is negative (0) and the overall sense is minimize (0),
                // then we are maximizing this objective (1).
                // If the coefficient is positive (1) and the overall sense is maximize (1),
                // then we are again maximizing this objective (1).
                // If the signs are opposite (maximizing a negative or minimizing a positive),
                // then we are minimizing the objective (0).
                objectiveSenses.put(objName, (coefficientSign == overallSense) ? 1 : 0);
            }

            // for ease of access, save the size of the objectives AL
            this.numObjectives = this.objectives.size();

        } catch (IloException e) {
            e.printStackTrace();
        }

    }

}

// TODO add functionality for hot start
// requires the hot start model and extracting ideal and worst case values
// TODO put single-obj solutions in primary output file (CSV)
// TODO error handling for a bad model file
// TODO error handling for bad CPLEX param values
// TODO error handling for bad algo param values
// TODO error handling for objectives not in conflict
// TODO allow for relative deltas
// TODO update manual to require all obj coeffs to have appropriate nonzero coefficients