package org.monarchinitiative.fhir2hpo.loinc.exception;


public class UnmappedValueStringException extends ConversionException {

	private static final long serialVersionUID = 1L;

	public UnmappedValueStringException() {
		super("The value string is not mapped to a known code.");
	}
	
	public UnmappedValueStringException(String message) {
		super(message);
	}

}
