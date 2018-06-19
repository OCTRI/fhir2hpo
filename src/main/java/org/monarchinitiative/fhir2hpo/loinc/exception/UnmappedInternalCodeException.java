package org.monarchinitiative.fhir2hpo.loinc.exception;


public class UnmappedInternalCodeException extends ConversionException {

	private static final long serialVersionUID = 1L;

	public UnmappedInternalCodeException() {
		super("The internal code is not mapped to HPO.");
	}
	
	public UnmappedInternalCodeException(String message) {
		super(message);
	}

}
