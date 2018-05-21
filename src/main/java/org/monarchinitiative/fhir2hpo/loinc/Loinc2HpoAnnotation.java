package org.monarchinitiative.fhir2hpo.loinc;

import java.util.HashMap;
import java.util.Map;

import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;
import org.monarchinitiative.fhir2hpo.loinc.exception.UnmappedInternalCodeException;

/**
 * This represents the complete HPO annotation for a given LOINC Id. Each internal code should be mapped to an {@link HpoTermWithNegation}.
 * 
 * @author yateam
 *
 */
public class Loinc2HpoAnnotation {
	
    private LoincId loincId;
	private LoincScale loincScale;
    //Map from internal code to term including negation
    private Map<String,HpoTermWithNegation> codeToHpoTerm;
    
    public static class Builder {

        private LoincId loincId = null;
        private LoincScale loincScale = null;
        private Map<String,HpoTermWithNegation> codeToHpoTerm = new HashMap<>();

        public Builder() {

        }

        /**
         * Set the LOINC Id
         * @param loincId
         */
        public Builder setLoincId(LoincId loincId) {

            this.loincId = loincId;
            return this;

        }

        /**
         * Set the LOINC scale
         * @param loincScale
         */
        public Builder setLoincScale(LoincScale loincScale) {

            this.loincScale = loincScale;
            return this;

        }

        /**
         * Add an annotation in the advanced mode.
         * @param code
         * @param annotation
         * @return
         */
        public Builder addMapping(String internalCode, HpoTermWithNegation term) {

            this.codeToHpoTerm.put(internalCode, term);
            return this;

        }

         public Loinc2HpoAnnotation build() {

            return new Loinc2HpoAnnotation(loincId,
                    loincScale,
                    codeToHpoTerm);

        }
    }
    
    private Loinc2HpoAnnotation(LoincId loincId, LoincScale loincScale, Map<String, HpoTermWithNegation> codeToHpoTerm) {
    	this.loincId = loincId;
    	this.loincScale = loincScale;
    	this.codeToHpoTerm = codeToHpoTerm;
    }
    
    
    /**
     * 
     * @return the LOINC Id
     */
    public LoincId getLoincId() {
		return loincId;
	}

	/**
	 * 
	 * @return the LOINC scale
	 */
    public LoincScale getLoincScale() {
		return loincScale;
	}

    /**
     * Given an internal code, return the corresponding HpoTerm or throw an exception.
     * @param code
     * @return the term with negation
     * @throws UnmappedInternalCodeException
     */
    public HpoTermWithNegation getHpoTermForInternalCode(String code) throws UnmappedInternalCodeException {
    	HpoTermWithNegation term = codeToHpoTerm.get(code);
    	if (term == null) {
    		throw new UnmappedInternalCodeException(loincId, code);
    	}
    	return term;
    }


}
