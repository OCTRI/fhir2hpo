package org.monarchinitiative.fhir2hpo.hpo;

import org.monarchinitiative.phenol.ontology.data.Term;

public class HpoTermWithNegation {

	private final Term hpoTerm;
	private final boolean isNegated;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hpoTerm == null) ? 0 : hpoTerm.hashCode());
		result = prime * result + (isNegated ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		HpoTermWithNegation other = (HpoTermWithNegation) obj;
		if (isNegated != other.isNegated) {
			return false;
		}
		if (hpoTerm == null && other.getHpoTerm() == null) {
			return true;
		}
		if ((hpoTerm == null && other.getHpoTerm() != null) || (hpoTerm != null && other.getHpoTerm() == null)) {
			return false;
		}
		return hpoTerm.getId().equals(other.getHpoTerm().getId());
	}
	
	@Override
	public String toString() {
		return hpoTerm.getName() + ":" + isNegated;
	}

}
