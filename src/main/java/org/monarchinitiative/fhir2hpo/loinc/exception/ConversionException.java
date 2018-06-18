package org.monarchinitiative.fhir2hpo.loinc.exception;


public abstract class ConversionException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ConversionException(String message) {
		super(message);
	}
	
}
