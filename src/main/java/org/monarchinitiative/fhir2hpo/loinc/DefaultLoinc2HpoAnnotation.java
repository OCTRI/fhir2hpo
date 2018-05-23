package org.monarchinitiative.fhir2hpo.loinc;

import java.util.HashMap;
import java.util.Map;

import org.hl7.fhir.dstu3.model.Observation;
import org.monarchinitiative.fhir2hpo.codesystems.CodeableConceptAnalyzer;
import org.monarchinitiative.fhir2hpo.codesystems.Loinc2HpoCodedValue;
import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;
import org.monarchinitiative.fhir2hpo.loinc.exception.ConflictingInternalCodesException;
import org.monarchinitiative.fhir2hpo.loinc.exception.UnmappedCodeableConceptException;
import org.monarchinitiative.fhir2hpo.loinc.exception.UnmappedInternalCodeException;

/**
 * This represents the default annotation implementation where a single observation is parsed
 * to determine the HPO mapping. It will try the following approaches in order:
 * - Find HPO through interpretation CodeableConcept
 * - Find HPO through value quantity
 * - Find HPO through value CodeableConcept
 * 
 * @author yateam
 *
 */
public class DefaultLoinc2HpoAnnotation implements Loinc2HpoAnnotation {
	
    private LoincId loincId;
	private LoincScale loincScale;
    //Map from internal code to term including negation
    private Map<Loinc2HpoCodedValue,HpoTermWithNegation> codeToHpoTerm;
    
    public static class Builder {

        private LoincId loincId = null;
        private LoincScale loincScale = null;
        private Map<Loinc2HpoCodedValue,HpoTermWithNegation> codeToHpoTerm = new HashMap<>();

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
        public Builder addMapping(Loinc2HpoCodedValue internalCode, HpoTermWithNegation term) {
            this.codeToHpoTerm.put(internalCode, term);
            return this;
        }

         public DefaultLoinc2HpoAnnotation build() {
            return new DefaultLoinc2HpoAnnotation(loincId,
                    loincScale,
                    codeToHpoTerm);
        }
    }
    
    private DefaultLoinc2HpoAnnotation(LoincId loincId, LoincScale loincScale, Map<Loinc2HpoCodedValue, HpoTermWithNegation> codeToHpoTerm) {
    	this.loincId = loincId;
    	this.loincScale = loincScale;
    	this.codeToHpoTerm = codeToHpoTerm;
    	
    	// If a "normal" term is mapped but not an "abnormal" term, create one.
    	if (codeToHpoTerm.containsKey(Loinc2HpoCodedValue.N) && !codeToHpoTerm.containsKey(Loinc2HpoCodedValue.A)) {
    		HpoTermWithNegation normalTerm = codeToHpoTerm.get(Loinc2HpoCodedValue.N);
    		codeToHpoTerm.put(Loinc2HpoCodedValue.A, new HpoTermWithNegation(normalTerm.getHpoTerm(), !normalTerm.isNegated()));
    	}
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

	@Override
	public HpoTermWithNegation convert(Observation observation) throws UnmappedCodeableConceptException, ConflictingInternalCodesException, UnmappedInternalCodeException {
		
		if (observation.hasInterpretation()) {
			Loinc2HpoCodedValue internalCode = CodeableConceptAnalyzer.getInternalCodeForCodeableConcept(observation.getInterpretation());
			return getHpoTermForInternalCode(internalCode);
		}
		return null;
	}

    /**
     * Given an internal code, return the corresponding HpoTerm or throw an exception.
     * @param code
     * @return the term with negation
     * @throws UnmappedInternalCodeException
     */
    private HpoTermWithNegation getHpoTermForInternalCode(Loinc2HpoCodedValue code) throws UnmappedInternalCodeException {
    	HpoTermWithNegation term = codeToHpoTerm.get(code);
    	if (term == null) {
    		throw new UnmappedInternalCodeException(loincId, code.name());
    	}
    	return term;
    }

}
