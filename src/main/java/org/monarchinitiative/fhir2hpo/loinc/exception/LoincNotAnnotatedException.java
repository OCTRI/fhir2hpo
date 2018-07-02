package org.monarchinitiative.fhir2hpo.loinc.exception;


public class LoincNotAnnotatedException extends LoincException {

	private static final long serialVersionUID = 1L;

	public LoincNotAnnotatedException() {
		super("The LOINC Code has not been annotated.");
	}
	
	public LoincNotAnnotatedException(String message) {
		super(message);
	}
	
}
