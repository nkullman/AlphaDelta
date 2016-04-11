/**
 * Implements an interface for the alpha-delta algorithm.
 * 
 * @author nkullman
 * @version %I%, %G%
 * @since Apr 11, 2016
 */

package mco.alphadelta.implementation;

import java.io.File;

import mco.alphadelta.framework.IADAlgoInterface;
import mco.alphadelta.framework.IADAlgoParameters;
import mco.alphadelta.framework.IADCPLEXParameters;

public class ADAlgoInterface implements IADAlgoInterface {
	
	IADAlgoParameters algoParams = null;
	IADCPLEXParameters cplexParams = null;
	File mcoModel = null;

}
