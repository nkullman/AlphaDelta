/**
 * Defines the interface for the set of parameters affecting the implementation of the alpha-delta algorithm.
 * This includes the planar tilt (alpha) and the minimum difference in objective values between solutions (delta).
 *
 * @author nkullman
 * @version %I%, %G%
 * @since Apr 11, 2016
 */
package mco.alphadelta.framework;

public interface IADAlgoParameters {

    /**
     *
     *
     * @return
     */
    double getAlpha_degrees();

    /**
     * Returns the current array of values for the delta parameters
     *
     * @return
     */
    double[] getDeltas();

    /**
     * Set the value for the alpha parameter (in degrees).
     *
     * @param alpha_degrees
     */
    void setAlpha_degrees(double alpha_degrees);

    /**
     * Set the value of the delta parameters for all non-principal objectives.
     *
     * @param deltas
     */
    void setDeltas(double[] deltas);

}
