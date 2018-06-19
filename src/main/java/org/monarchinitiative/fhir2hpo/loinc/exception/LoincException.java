package org.monarchinitiative.fhir2hpo.loinc.exception;


public abstract class LoincException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public LoincException(String message) {
		super(message);
	}

}
