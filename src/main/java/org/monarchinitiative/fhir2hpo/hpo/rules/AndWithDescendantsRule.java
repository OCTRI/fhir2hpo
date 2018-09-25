package org.monarchinitiative.fhir2hpo.hpo.rules;

import java.util.Set;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlScript;
import org.apache.commons.jexl3.MapContext;
import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;

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
	
	/**
	 * Construct an AND WITH DESCENDANTS rule. s1 and s2 are sets of an HPO term and all its descendants.
	 * h3 is inferred if the term or any of its descendants are found.
	 * 
	 * @param s1
	 * @param s2
	 * @param h3
	 */
	public AndWithDescendantsRule(Set<HpoTermWithNegation> s1, Set<HpoTermWithNegation> s2, HpoTermWithNegation h3) {
		jexlContext = new MapContext();
		jexlContext.set("S1", s1);
		jexlContext.set("S2", s2);
		jexlContext.set("H3", h3);
	}
	
	@Override
	public JexlScript getRule() {
		return AND_WITH_DESCENDANTS;
	}

	@Override
	public JexlContext getContext() {
		return jexlContext;
	}
	
}
