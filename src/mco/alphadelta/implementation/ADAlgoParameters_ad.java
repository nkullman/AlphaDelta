package mco.alphadelta.implementation;

import mco.alphadelta.framework.IADAlgoParameters;

/**
 * Created by Nick on 4/22/2016.
 */
public class ADAlgoParameters_ad implements IADAlgoParameters{
    private double alpha_degrees = -1;
    private double[] deltas = null;

    public void setAlpha_degrees(double alpha_degrees){
        this.alpha_degrees = alpha_degrees;
    }

    public void setDeltas(double[] deltas){
        this.deltas = deltas;
    }

    public double getAlpha_degrees(){
        return this.alpha_degrees;
    }

    public double[] getDeltas(){
        return this.deltas;
    }
}
