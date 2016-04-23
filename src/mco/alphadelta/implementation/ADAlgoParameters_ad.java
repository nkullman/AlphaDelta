package mco.alphadelta.implementation;

import mco.alphadelta.framework.IADAlgoParameters;

import java.util.ArrayList;

/**
 * Created by Nick on 4/22/2016.
 */
public class ADAlgoParameters_ad implements IADAlgoParameters{
    private double alpha_degrees = -1;
    private ArrayList<Double> deltas = null;

    public void setAlpha_degrees(double alpha_degrees){
        this.alpha_degrees = alpha_degrees;
    }

    public void setDeltas(ArrayList<Double> deltas) {
        this.deltas = deltas;
    }

    public double getAlpha_degrees(){
        return this.alpha_degrees;
    }

    public ArrayList<Double> getDeltas() {
        return this.deltas;
    }
}
