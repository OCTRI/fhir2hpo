package org.monarchinitiative.fhir2hpo.loinc.exception;

import java.util.Set;
import java.util.stream.Collectors;

import org.monarchinitiative.fhir2hpo.codesystems.Loinc2HpoCodedValue;

public class ConflictingInternalCodesException extends Exception {

	private static final long serialVersionUID = 1L;

	public ConflictingInternalCodesException(Set<Loinc2HpoCodedValue> distinctCodes) {
		super("The CodeableConcept resolves to multiple internal codes: " + distinctCodes.stream().map(Loinc2HpoCodedValue::toString).collect(Collectors.joining()));
	}

}
