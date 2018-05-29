package org.monarchinitiative.fhir2hpo.loinc.exception;

public class ReferenceRangeNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public ReferenceRangeNotFoundException() {
		super("No reference range was found on the observation.");
	}

}
