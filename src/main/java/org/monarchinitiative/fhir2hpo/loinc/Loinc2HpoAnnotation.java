package org.monarchinitiative.fhir2hpo.loinc;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.exceptions.FHIRException;
import org.monarchinitiative.fhir2hpo.hpo.HpoConversionResult;
import org.monarchinitiative.fhir2hpo.loinc.exception.ConversionException;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincException;

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
     * @throws LoincException
     * @throws ConversionException
     * @throws FHIRException
     */
    public HpoConversionResult convert(Observation observation);

}
