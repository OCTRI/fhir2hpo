package org.monarchinitiative.fhir2hpo.loinc.exception;


public class MissingValueQuantityException extends ConversionException {

	private static final long serialVersionUID = 1L;

	public MissingValueQuantityException() {
		super("The observation does not have a value quantity.");
	}
	
}
