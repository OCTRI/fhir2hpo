package org.monarchinitiative.fhir2hpo.loinc.exception;


public class MissingInterpretationException extends ConversionException {

	private static final long serialVersionUID = 1L;

	public MissingInterpretationException() {
		super("The observation does not have an interpretation.");
	}
	
}
