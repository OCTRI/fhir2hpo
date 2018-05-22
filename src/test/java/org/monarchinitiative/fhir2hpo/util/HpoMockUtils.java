package org.monarchinitiative.fhir2hpo.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;
import org.monarchinitiative.phenol.ontology.data.Term;

public class HpoMockUtils {
	
	/**
	 * Mock an HPO Term with just the name
	 * @param name
	 * @return
	 */
	public static Term mockTerm(String name) {
		Term hpoTerm = mock(Term.class);
		when(hpoTerm.getName()).thenReturn(name);
		return hpoTerm;
	}
	
	/**
	 * Mock an HpoTermWithNegation
	 * @param name the answer to hpoTerm.getName()
	 * @param isNegated whether the term should be negated
	 * @return
	 */
	public static HpoTermWithNegation mockHpoTermWithNegation(String name, boolean isNegated) {
		return new HpoTermWithNegation(mockTerm(name), isNegated);
	}

}
