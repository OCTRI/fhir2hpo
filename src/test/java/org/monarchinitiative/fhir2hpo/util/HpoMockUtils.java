package org.monarchinitiative.fhir2hpo.util;

import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class HpoMockUtils {
	
	/**
	 * Mock an HPO Term Id
	 * @param idWithPrefix
	 * @return
	 */
	public static TermId mockTermId(String idWithPrefix) {
		// TermId is a final class and cannot be mocked with Mockito
		TermId hpoTermId = TermId.constructWithPrefix(idWithPrefix);
		return hpoTermId;
	}
	
	/**
	 * Mock an HpoTermWithNegation
	 * @param idWithPrefix the answer to hpoTerm.getIdWithPrefix()
	 * @param isNegated whether the term should be negated
	 * @return
	 */
	public static HpoTermWithNegation mockHpoTermWithNegation(String idWithPrefix, boolean isNegated) {
		return new HpoTermWithNegation(mockTermId(idWithPrefix), isNegated);
	}

}
