package org.monarchinitiative.fhir2hpo.loinc.exception;


public class ConversionException extends Exception {
	
	private static final long serialVersionUID = 1L;

	private ConversionExceptionType type;
	private String message;
	
	public enum ConversionExceptionType{
		MISMATCHED_LOINC_ID("The observation contains the wrong LoincId for the annotation."),
		AMBIGUOUS_REFERENCE_RANGE("Cannot handle multiple reference ranges."),
		CONFLICTING_INTERNAL_CODES("The observation resolves to multiple internal codes."),
		REFERENCE_RANGE_NOT_FOUND("No reference range was found on the observation."),
		UNMAPPED_CODEABLE_CONCEPT("Could not find any mapped codes."),
		UNMAPPED_INTERNAL_CODE("The internal code is not mapped to HPO.");
		
		private String message;
	
		private ConversionExceptionType(String message) {
			this.message = message;
		}
	}
	
	public ConversionException(ConversionExceptionType type) {
		this.type = type;
		this.message = type.message;
	}

	public ConversionException(ConversionExceptionType type, String customMessage) {
		this.type = type;
		this.message = customMessage;
	}

	@Override
	public String getMessage() {
		return message;
	}
	
	public ConversionExceptionType getType() {
		return type;
	}

}
