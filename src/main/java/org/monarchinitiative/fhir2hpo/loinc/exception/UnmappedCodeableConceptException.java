package org.monarchinitiative.fhir2hpo.loinc.exception;


public class UnmappedCodeableConceptException extends ConversionException {

	private static final long serialVersionUID = 1L;

	public UnmappedCodeableConceptException() {
		super("Could not find any mapped codes.");
	}
	
	public UnmappedCodeableConceptException(String message) {
		super(message);
	}

}
