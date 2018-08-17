package org.monarchinitiative.fhir2hpo.util;

import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class HpoMockUtils {
	
	/**
	 * Get an HpoTermWithNegation
	 * @param idWithPrefix the answer to term.getIdWithPrefix()
	 * @param isNegated whether the term should be negated
	 * @return
	 */
	public static HpoTermWithNegation getHpoTermWithNegation(String idWithPrefix, boolean isNegated) {
		return new HpoTermWithNegation(TermId.constructWithPrefix(idWithPrefix), isNegated);
	}

}
