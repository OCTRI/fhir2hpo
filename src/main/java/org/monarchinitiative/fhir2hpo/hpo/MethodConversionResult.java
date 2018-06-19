package org.monarchinitiative.fhir2hpo.hpo;

public class MethodConversionResult {
	
	// Description of the method used
	final String method;
	HpoTermWithNegation hpoTerm;
	Exception exception;
	
	public MethodConversionResult(String method) {
		this.method = method;
	}

	public String getMethod() {
		return method;
	}

	public void succeed(HpoTermWithNegation hpoTerm) {
		this.hpoTerm = hpoTerm;
		this.exception = null;
	}
	
	public void fail(Exception exception) {
		this.hpoTerm = null;
		this.exception = exception;
	}
	
	public Boolean hasTerm() {
		return hpoTerm != null;
	}
	
	public HpoTermWithNegation getTerm() {
		return hpoTerm;
	}
	
	public Boolean hasException() {
		return exception != null;
	}
	
	public Exception getException() {
		return exception;
	}

}
