package org.monarchinitiative.fhir2hpo.loinc;

import org.hl7.fhir.dstu3.model.Observation;
import org.monarchinitiative.fhir2hpo.hpo.HpoConversionResult;

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
     */
    public HpoConversionResult convert(Observation observation);

}
