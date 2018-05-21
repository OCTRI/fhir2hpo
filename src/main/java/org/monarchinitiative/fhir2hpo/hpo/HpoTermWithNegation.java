package org.monarchinitiative.fhir2hpo.hpo;

import org.monarchinitiative.phenol.ontology.data.Term;

public class HpoTermWithNegation {
	
	private Term hpoTerm;
	private boolean isNegated;
	
	public HpoTermWithNegation(Term hpoTerm, boolean isNegated) {
		this.hpoTerm = hpoTerm;
		this.isNegated = isNegated;
	}

	public Term getHpoTerm() {
		return hpoTerm;
	}

	public boolean isNegated() {
		return isNegated;
	}

}
