package org.monarchinitiative.fhir2hpo.loinc.exception;

public class UnmappedCodeableConceptException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnmappedCodeableConceptException() {
		super("Could not find any mapped codes");
	}

}
