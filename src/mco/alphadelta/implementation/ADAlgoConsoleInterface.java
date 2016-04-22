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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ADAlgoConsoleInterface implements IADAlgoInterface {

    IADAlgoParameters algoParams = null;
    IADSolverParameters solverParams = null;
    File mcoModel = null;
    IADAlgoSolver algoSolver = null;
    File outputPath = null;

    public IADAlgoParameters getADAlgorithmParameters() {
        IADAlgoParameters algoParameters = new ADAlgoParameters_ad();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter a value for alpha (in degrees). Default is 0.01:");

        try {
            algoParameters.setAlpha_degrees(Double.parseDouble(reader.readLine()));
            System.out.println("Alpha set.");
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Enter values for the objectives' deltas.");
        System.out.println("These should be listed in the same order as the objectives in your model file.");
        System.out.println("Values should be separated by a space");

        try {
            String[] deltaStrings = reader.readLine().split(" ");
            double[] deltas = new double[deltaStrings.length];

            for (int i = 0; i < deltaStrings.length; i++)
                deltas[i] = Double.parseDouble(deltaStrings[i]);

            algoParameters.setDeltas(deltas);
            System.out.println("Deltas set.");
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return algoParameters;
    }

    public void setADAlgorithmParameters(IADAlgoParameters algoParameters) {
        this.algoParams = algoParameters;
    }

    public IADSolverParameters getADSolverParameters() {

        IADSolverParameters solverParameters;
        if (this.algoSolver.getType().equalsIgnoreCase("cplex")){
            solverParameters = new ADSolverCPLEXParameters();
        } else {
            System.out.println("Currently assigned solver type unrecognized. Constructing a set of parameters for CPLEX.");
            solverParameters = new ADSolverCPLEXParameters();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter a parameter-value pair, separated by a space,");
        System.out.println("or just hit enter to finish setting parameters.");
        try {
            String input = reader.readLine();
            while (input.length() > 0){
                String[] kvPair = input.split(" ");
                if (kvPair.length != 2){
                    System.out.println("Improper input format. Please enter a parameter and its value separated by a space .");
                    System.out.println("Example:");
                    System.out.println("timelimit 7200");
                    System.out.println();
                } else {
                    solverParameters.addParam(kvPair[0],kvPair[1]);
                    System.out.println("Parameter stored.");
                    System.out.println();
                }
                System.out.println("Enter a parameter-value pair, separated by a space,");
                System.out.println("or just hit enter to finish setting parameters.");
                input = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return solverParameters;
    }

    public void setADSolverParameters(IADSolverParameters solverParameters) {
        this.solverParams = solverParameters;
    }

    public File getMCOModel() {
        File modelFile = null;

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the full filepath to model file:");

        try {
            modelFile = new File(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return modelFile;
    }

    public void setMCOModel(File mcoModel) {
        this.mcoModel = mcoModel;
    }

    public void setADSolver(IADAlgoSolver algoSolver) {
        this.algoSolver = algoSolver;
    }

    public IADAlgoSolver getADSolver() {
        IADAlgoSolver solver = null;
        String solverString = null;

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the name of the solver you would like to use:");

        try {
            solverString = reader.readLine();
            if (solverString.equalsIgnoreCase("cplex")){
                solver = new ADAlgoSolver_CPLEX();
            } else {
                System.out.println("Solver type not recognized. Using default solver: CPLEX.");
                solver = new ADAlgoSolver_CPLEX();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return solver;
    }

    public File getOutputDestination() {
        File outputPath = null;

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the directory where you'd like to store output:");

        try {
            outputPath = new File(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputPath;
    }

    public void setOutputDestination(File outputDestination) {
        this.outputPath = outputDestination;
    }

    public boolean runSolver() {
        if (this.algoSolver == null) {
            System.out.println("Error: No solver assigned. Please first assign a solver, then try again.");
            return false;
        } else if (this.mcoModel == null) {
            System.out.println("Error: No model file assigned. Please first assign a model file, then try again");
            return false;
        }

        this.algoSolver.setOutputPath(this.outputPath);
        this.algoSolver.setAlgoParameters(this.algoParams);
        this.algoSolver.setSolverParameters(this.solverParams);

        return this.algoSolver.solve(this.mcoModel);
    }

    public void prepAlgorithm() {
        setMCOModel(getMCOModel());
        setOutputDestination(getOutputDestination());
        setADAlgorithmParameters(getADAlgorithmParameters());
        setADSolver(getADSolver());
        setADSolverParameters(getADSolverParameters());

    }
}
