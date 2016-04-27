package mco.alphadelta.implementation;

import mco.alphadelta.framework.IADSolverParameters;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Nick on 4/22/2016.
 */
public class ADSolverCPLEXParameters implements IADSolverParameters{
    private Map<String,String> paramName_paramValue = new HashMap<>();

    public void addParam(String paramName, String paramValue){
        this.paramName_paramValue.put(paramName,paramValue);
    }

    public String getParam(String paramName) {
        return this.paramName_paramValue.get(paramName);
    }

    public Set<String> getParamSet() {
        return this.paramName_paramValue.keySet();
    }
}
