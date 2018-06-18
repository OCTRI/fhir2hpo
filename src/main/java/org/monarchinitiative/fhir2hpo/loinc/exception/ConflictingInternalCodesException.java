package org.monarchinitiative.fhir2hpo.loinc.exception;


public class ConflictingInternalCodesException extends ConversionException {

	private static final long serialVersionUID = 1L;

	public ConflictingInternalCodesException() {
		super("The observation resolves to multiple internal codes.");
	}
	
	public ConflictingInternalCodesException(String message) {
		super(message);
	}

}
