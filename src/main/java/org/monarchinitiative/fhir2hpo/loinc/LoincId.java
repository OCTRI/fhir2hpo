package org.monarchinitiative.fhir2hpo.loinc;

import org.monarchinitiative.fhir2hpo.loinc.exception.LoincException;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincException.LoincExceptionType;

public class LoincId {
	
	String code;
	
	/**
	 * Construct a LoincId and throw an exception if the code is not understood.
	 * @param code
	 * @throws LoincException the exception thrown if the code is not of the format "i-j" where i and j are integers
	 */
	public LoincId(String code) throws LoincException {
		
		this.code = code;
		if (!code.matches("^\\d+-\\d+$")) {
			throw new LoincException(LoincExceptionType.MALFORMED_LOINC_CODE, "The code " + code + " is not a valid LOINC format.");
		}
	}
	
	public String getCode() {
		return code;
	}
	
    @Override
	public int hashCode() {
		return code.hashCode();
	}

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof LoincId)) {
            return false;
        }
        LoincId other = (LoincId) o;
        return this.code.equals(other.getCode());
    }
    
    @Override
    public String toString() {
    	return code;
    }

}
