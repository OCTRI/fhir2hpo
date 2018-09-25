package org.monarchinitiative.fhir2hpo.hpo.rules;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlScript;
import org.apache.commons.jexl3.MapContext;
import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;

public class AndRule implements HpoInferenceRule {

	private static final JexlScript AND_RULE = ENGINE.createScript(
		"var containsLeft=false;" +
		"var containsRight=false;" +
		"for(item : L) {" +
		"  if (item == H1) containsLeft=true;" +
		"  if (item == H2) containsRight=true;" +
		"} " +
		"if (containsLeft and containsRight) H3"
	);
	
	JexlContext jexlContext;
	
	/**
	 * Construct an AND rule where the existence of h1 and h2 should infer h3
	 * @param h1
	 * @param h2
	 * @param h3
	 */
	public AndRule(HpoTermWithNegation h1, HpoTermWithNegation h2, HpoTermWithNegation h3) {
		jexlContext = new MapContext();
		jexlContext.set("H1", h1);
		jexlContext.set("H2", h2);
		jexlContext.set("H3", h3);
	}
	
	@Override
	public JexlScript getRule() {
		return AND_RULE;
	}

	@Override
	public JexlContext getContext() {
		return jexlContext;
	}

}
