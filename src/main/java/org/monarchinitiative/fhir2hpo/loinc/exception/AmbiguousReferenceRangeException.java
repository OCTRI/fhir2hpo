package org.monarchinitiative.fhir2hpo.loinc.exception;


public class AmbiguousReferenceRangeException extends ConversionException {

	private static final long serialVersionUID = 1L;

	public AmbiguousReferenceRangeException() {
		super("Cannot handle multiple reference ranges.");
	}
	
	public AmbiguousReferenceRangeException(String message) {
		super(message);
	}

}
