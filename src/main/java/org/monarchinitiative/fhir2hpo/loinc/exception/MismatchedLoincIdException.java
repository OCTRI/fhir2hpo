package org.monarchinitiative.fhir2hpo.loinc.exception;


public class MismatchedLoincIdException extends ConversionException {

	private static final long serialVersionUID = 1L;

	public MismatchedLoincIdException() {
		super("The observation contains the wrong LoincId for the annotation.");
	}
	
	public MismatchedLoincIdException(String message) {
		super(message);
	}

}
