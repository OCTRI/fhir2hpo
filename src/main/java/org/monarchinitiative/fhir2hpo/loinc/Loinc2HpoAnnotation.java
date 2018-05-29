package org.monarchinitiative.fhir2hpo.loinc;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.exceptions.FHIRException;
import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;
import org.monarchinitiative.fhir2hpo.loinc.exception.AmbiguousReferenceRangeException;
import org.monarchinitiative.fhir2hpo.loinc.exception.ConflictingInternalCodesException;
import org.monarchinitiative.fhir2hpo.loinc.exception.ReferenceRangeNotFoundException;
import org.monarchinitiative.fhir2hpo.loinc.exception.UnmappedCodeableConceptException;
import org.monarchinitiative.fhir2hpo.loinc.exception.UnmappedInternalCodeException;

/**
 * This interface defines the complete HPO annotation for a given LOINC Id. It provides a way to convert
 * an observation to an HPO Term
 * 
 * @author yateam
 *
 */
public interface Loinc2HpoAnnotation {
	
    /**
     * 
     * @return the LOINC Id
     */
    public LoincId getLoincId();

	/**
	 * 
	 * @return the LOINC scale
	 */
    public LoincScale getLoincScale();
    
    /**
     * 
     * @param observation
     * @return
     * @throws UnmappedCodeableConceptException
     * @throws ConflictingInternalCodesException
     * @throws UnmappedInternalCodeException
     * @throws ReferenceRangeNotFoundException
     * @throws AmbiguousReferenceRangeException
     * @throws FHIRException
     */
    public HpoTermWithNegation convert(Observation observation) throws UnmappedCodeableConceptException, ConflictingInternalCodesException, UnmappedInternalCodeException, ReferenceRangeNotFoundException, AmbiguousReferenceRangeException, FHIRException;

}
