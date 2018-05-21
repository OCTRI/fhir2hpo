package org.monarchinitiative.fhir2hpo.loinc.exception;

import org.monarchinitiative.fhir2hpo.loinc.LoincId;

public class UnmappedInternalCodeException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnmappedInternalCodeException(LoincId loincId, String code) {
		super("The internal code " + code + " has no HPO mapping for LOINC " + loincId.getCode());
	}

}
