package org.monarchinitiative.fhir2hpo.loinc.exception;

import java.util.Set;
import java.util.stream.Collectors;

public class ConflictingInternalCodesException extends Exception {

	private static final long serialVersionUID = 1L;

	public ConflictingInternalCodesException(Set<String> distinctCodes) {
		super("The CodeableConcept resolves to multiple internal codes: " + distinctCodes.stream().collect(Collectors.joining()));
	}

}
