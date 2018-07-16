package org.monarchinitiative.fhir2hpo.loinc.exception;


public class MissingValueStringException extends ConversionException {

	private static final long serialVersionUID = 1L;

	public MissingValueStringException() {
		super("The observation does not have a value quantity.");
	}
	
}
