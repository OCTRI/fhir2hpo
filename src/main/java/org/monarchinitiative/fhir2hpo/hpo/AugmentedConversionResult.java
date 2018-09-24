package org.monarchinitiative.fhir2hpo.hpo;

/**
 * This class represents an augmented conversion result generated by analyzing the relationships
 * between HPO Terms found in the first-level analysis.
 * 
 * @author yateam
 *
 */
public class AugmentedConversionResult {
	
	HpoTermWithNegation hpoTerm;
	
	// TODO: Allow multiple terms? Record evidence?
	public AugmentedConversionResult(HpoTermWithNegation hpoTerm) {
		this.hpoTerm = hpoTerm;
	}
	
	public HpoTermWithNegation getHpoTerm() {
		return hpoTerm;
	}

}
