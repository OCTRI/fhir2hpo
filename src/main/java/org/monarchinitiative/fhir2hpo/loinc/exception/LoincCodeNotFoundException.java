package org.monarchinitiative.fhir2hpo.loinc.exception;


public class LoincCodeNotFoundException extends LoincException {

	private static final long serialVersionUID = 1L;

	public LoincCodeNotFoundException() {
		super("The observation does not contain a LOINC code.");
	}
	
}
