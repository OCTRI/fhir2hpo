package org.monarchinitiative.fhir2hpo.hpo;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlScript;

public class AugmentationRules {
	
	private static final JexlEngine ENGINE = new JexlBuilder().cache(512).strict(true).silent(false).create();

	// if (H1 and H2) in L, return H3
	// To use the AND_RULE create a JexlContext, set L, H1, H2, and H3 and evaluate. L is a list of HpoTermsWithNegation
	// and H1, H2, and H3 are HpoTermsWithNegation.
	public static final JexlScript AND_RULE = ENGINE.createScript(
		"var containsLeft=false;" +
		"var containsRight=false;" +
		"for(item : L) {" +
		"  if (item == H1) containsLeft=true;" +
		"  if (item == H2) containsRight=true;" +
		"} " +
		"if (containsLeft and containsRight) H3"
	);
	
	// if (any in L1 in L) and (any in L2 in L), return H3
	// To use the AND_WITH_DESCENDANTS_RULE create a JexlContext, set L, L1, L2, and H3 and evaluate. L is a list of
	// HpoTermsWithNegation. L1 and L2 are lists of HpoTermsWithNegation and all children. H3 is the resulting term. 
	// and H1 and H2 are of the same type as items in that list.
	public static final JexlScript AND_WITH_DESCENDANTS = ENGINE.createScript(
		"var containsLeft=false;" +
		"var containsRight=false;" +
		"for(term : L) {" +
		"  for (leftTerm: L1) {" +
		"    if (term == leftTerm) containsLeft=true;" +
		"  }; " +
		"  for (rightTerm : L2) {" +
		"    if (term==rightTerm) containsRight=true;" +
		"  }" +
		"}; " +
		"if (containsLeft and containsRight) H3"
	);

}
