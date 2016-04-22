package mco.alphadelta.implementation;

import mco.alphadelta.framework.IADSolverParameters;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 4/22/2016.
 */
public class ADSolverCPLEXParameters implements IADSolverParameters{
    private Map<String,String> paramName_paramValue = new HashMap<>();

    public void addParam(String paramName, String paramValue){
        this.paramName_paramValue.put(paramName,paramValue);
    }
}
