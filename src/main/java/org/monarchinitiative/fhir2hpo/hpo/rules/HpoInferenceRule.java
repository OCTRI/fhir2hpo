package org.monarchinitiative.fhir2hpo.hpo.rules;

import java.util.List;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlScript;
import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;

/**
 * Defines a rule where a set of HPO terms may infer an additional phenotype
 * @author yateam
 *
 */
public interface HpoInferenceRule {

	static final JexlEngine ENGINE = new JexlBuilder().cache(512).strict(true).silent(false).create();

	JexlScript getRule();
	JexlContext getContext();
	
	/**
	 * Given the set of terms found in an observation, return an inferred term or null if no inference is made
	 * @param terms
	 * @return
	 */
	public default HpoTermWithNegation evaluate(List<HpoTermWithNegation> terms) {
		getContext().set("L", terms);
		Object o = getRule().execute(getContext());
		return (o != null) ? (HpoTermWithNegation) o : null;
	}

}
