package org.monarchinitiative.fhir2hpo.loinc.exception;


public class ReferenceRangeNotFoundException extends ConversionException {

	private static final long serialVersionUID = 1L;

	public ReferenceRangeNotFoundException() {
		super("No reference range was found on the observation.");
	}
	
	public ReferenceRangeNotFoundException(String message) {
		super(message);
	}

}
