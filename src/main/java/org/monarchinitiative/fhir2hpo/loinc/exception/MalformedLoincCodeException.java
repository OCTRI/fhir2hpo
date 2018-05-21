package org.monarchinitiative.fhir2hpo.loinc.exception;

public class MalformedLoincCodeException extends Exception {

	private static final long serialVersionUID = 1L;

	public MalformedLoincCodeException(String code) {
		super("The code " + code + " is not a valid LOINC format.");
	}

}
