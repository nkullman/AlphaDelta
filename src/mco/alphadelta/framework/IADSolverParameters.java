/**
 * Defines the interface for the set of parameters that govern the solver during the execution
 * of the alpha-delta algorithm. Examples include optimality tolerance and time limit.
 *
 * @author nkullman
 * @version %I%, %G%
 * @since Apr 11, 2016
 */
package mco.alphadelta.framework;

public interface IADSolverParameters {

    public void addParam(String paramName, String paramValue);

}
