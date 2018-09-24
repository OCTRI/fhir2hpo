package org.monarchinitiative.fhir2hpo.hpo;

import org.monarchinitiative.phenol.ontology.data.TermId;

public class HpoTermWithNegation {

	private final TermId hpoTermId;
	private final boolean isNegated;

	public HpoTermWithNegation(TermId hpoTermId, boolean isNegated) {
		this.hpoTermId = hpoTermId;
		this.isNegated = isNegated;
	}

	public TermId getHpoTermId() {
		return hpoTermId;
	}

	public boolean isNegated() {
		return isNegated;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hpoTermId == null) ? 0 : hpoTermId.hashCode());
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
		if (hpoTermId == null && other.getHpoTermId() == null) {
			return true;
		}
		if ((hpoTermId == null && other.getHpoTermId() != null) || (hpoTermId != null && other.getHpoTermId() == null)) {
			return false;
		}
		return hpoTermId.getId().equals(other.getHpoTermId().getId());
	}
	
	public String toString() {
		return (isNegated ? "EXCLUDED: ":"") + hpoTermId.getIdWithPrefix();
	}
	
}
