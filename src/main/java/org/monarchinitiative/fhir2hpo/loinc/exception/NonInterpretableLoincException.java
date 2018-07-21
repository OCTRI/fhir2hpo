package org.monarchinitiative.fhir2hpo.loinc.exception;


public class NonInterpretableLoincException extends Exception {

	private static final long serialVersionUID = 1L;

	public NonInterpretableLoincException(String reason) {
		super(reason);
	}

}
