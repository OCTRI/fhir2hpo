package org.monarchinitiative.fhir2hpo.loinc.exception;


public class ConflictingLoincCodesException extends LoincException {

	private static final long serialVersionUID = 1L;

	public ConflictingLoincCodesException() {
		super("The observation contains more than one LOINC code.");
	}
	
}
