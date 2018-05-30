package org.monarchinitiative.fhir2hpo.loinc.exception;


public class LoincException extends Exception {
	
	private static final long serialVersionUID = 1L;

	private LoincExceptionType type;
	private String message;
	
	public enum LoincExceptionType{
		MALFORMED_LOINC_CODE("The code is not a valid LOINC format."),
		LOINC_CODE_NOT_FOUND("The observation does not contain a LOINC code."),
		CONFLICTING_LOINC_CODES("The observation contains more than one LOINC code.");
		
		private String message;
	
		private LoincExceptionType(String message) {
			this.message = message;
		}
	}
	
	public LoincException(LoincExceptionType type) {
		this.type = type;
		this.message = type.message;
	}

	public LoincException(LoincExceptionType type, String customMessage) {
		this.type = type;
		this.message = customMessage;
	}

	@Override
	public String getMessage() {
		return message;
	}
	
	public LoincExceptionType getType() {
		return type;
	}

}
