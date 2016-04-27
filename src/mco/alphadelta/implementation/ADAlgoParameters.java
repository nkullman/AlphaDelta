package mco.alphadelta.implementation;

import mco.alphadelta.framework.IADAlgoParameters;
import mco.alphadelta.framework.IADSolverParameters;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by nkullman on 4/22/2016.
 */
public class ADAlgoParameters implements IADAlgoParameters {

    private double alpha_degrees = Double.NaN;
    private ArrayList<Double> deltas = null;
    private boolean relativeDeltas = false;
    private boolean printLogFiles = false;
    private boolean printSolFiles = true;
    private boolean hotStart = false;
    private File hotStartModel = null;
    private IADSolverParameters solverParamsForIdealConstruction = null;

    public void setAlpha_degrees(double alpha_degrees) {
        this.alpha_degrees = alpha_degrees;
    }

    public void setDeltas(ArrayList<Double> deltas) {
        this.deltas = deltas;
    }

    public double getAlpha_degrees() {
        return this.alpha_degrees;
    }

    public ArrayList<Double> getDeltas() {
        ArrayList<Double> clone = new ArrayList<>();

        for (double e : this.deltas) clone.add(e);

        return clone;
    }

    public void setPrintLogFiles(boolean printLogFiles) {
        this.printLogFiles = printLogFiles;
    }

    public void setPrintSolFiles(boolean printSolFiles) {
        this.printSolFiles = printSolFiles;
    }

    public boolean willPrintLogFiles() {
        return printLogFiles;
    }

    public boolean willPrintSolFiles() {
        return printSolFiles;
    }

    public boolean willHotStart() {
        return hotStart;
    }

    public File getHotStartModel() {
        return hotStartModel;
    }

    public void setHotStartModel(File hotStartModel) {
        this.hotStart = true;
        this.hotStartModel = hotStartModel;
    }

    public boolean areRelativeDeltas() {
        return relativeDeltas;
    }

    public void setRelativeDeltas(boolean relativeDeltas) {
        // TODO if any delta greater than 100, this is not possible
        // in that case, get the user to re-enter delta values
        this.relativeDeltas = relativeDeltas;
    }

    public void setSolverParamsForIdealConstruction(IADSolverParameters solverParamsForIdealConstruction) {
        this.solverParamsForIdealConstruction = solverParamsForIdealConstruction;
        // TODO build method in interface to set ideal solver params
        // can take advantage of getADSolverParameters method
    }

    public IADSolverParameters getSolverParamsForIdealConstruction() {
        return this.solverParamsForIdealConstruction;
    }
}
