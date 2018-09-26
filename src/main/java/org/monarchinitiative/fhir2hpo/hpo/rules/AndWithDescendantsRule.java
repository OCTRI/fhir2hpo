package org.monarchinitiative.fhir2hpo.hpo.rules;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlScript;
import org.apache.commons.jexl3.MapContext;
import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;
import org.monarchinitiative.fhir2hpo.service.HpoService;

public class AndWithDescendantsRule implements HpoInferenceRule {

	public static final JexlScript AND_WITH_DESCENDANTS = ENGINE.createScript(
		"var containsLeft=false;" +
		"var containsRight=false;" +
		"for(term : L) {" +
		"  for (leftTerm: S1) {" +
		"    if (term == leftTerm) containsLeft=true;" +
		"  }; " +
		"  for (rightTerm : S2) {" +
		"    if (term==rightTerm) containsRight=true;" +
		"  }" +
		"}; " +
		"if (containsLeft and containsRight) H3"
	);
	
	JexlContext jexlContext;
	
	private static final String INPUT_S1 = "S1";
	private static final String INPUT_S2 = "S2";
	private static final String OUTPUT_H3 = "H3";
	
	// Save the original HPO terms to use when describing
	HpoTermWithNegation h1;
	HpoTermWithNegation h2;
	
	/**
	 * Construct an AND WITH DESCENDANTS rule. s1 and s2 are sets of an HPO term and all its descendants.
	 * h3 is inferred if the term or any of its descendants are found.
	 * 
	 * @param s1
	 * @param s2
	 * @param h3
	 */
	public AndWithDescendantsRule(HpoService hpoService, HpoTermWithNegation h1, HpoTermWithNegation h2, HpoTermWithNegation h3) {
		this.h1 = h1;
		this.h2 = h2;
		jexlContext = new MapContext();
		jexlContext.set(INPUT_S1, hpoService.getChildren(h1));
		jexlContext.set(INPUT_S2, hpoService.getChildren(h2));
		jexlContext.set(OUTPUT_H3, h3);
	}
	
	@Override
	public JexlScript getRule() {
		return AND_WITH_DESCENDANTS;
	}

	@Override
	public JexlContext getContext() {
		return jexlContext;
	}

	@Override
	public String getDescription() {
		return h1 + " OR ANY DESCENDANT AND " + 
			h2 + " OR ANY DESCENDANT INFERS " +
			(HpoTermWithNegation) jexlContext.get(OUTPUT_H3);
	}
	
}
