package org.monarchinitiative.fhir2hpo.loinc.exception;

public class AmbiguousReferenceRangeException extends Exception {

	private static final long serialVersionUID = 1L;

	public AmbiguousReferenceRangeException() {
		super("Cannot handle multiple reference ranges");
	}

}
