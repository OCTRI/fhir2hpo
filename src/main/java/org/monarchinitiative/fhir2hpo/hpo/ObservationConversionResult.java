package org.monarchinitiative.fhir2hpo.hpo;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.dstu3.model.Observation;

/**
 * The ObservationConversionResult consists of the original observation, a collection of LoincConversionResults
 * corresponding to each LOINC found in the observation, and a collection of inferred conversion results from analyzing
 * the LOINC specific HPO terms. LOINCs can be found in both the code section and components
 * section of the observation.
 * 
 * @author yateam
 *
 */
public class ObservationConversionResult {
	
	private Observation observation;
	private List<LoincConversionResult> loincConversionResults;
	private List<InferredConversionResult> inferredConversionResults;
	
	public ObservationConversionResult(Observation observation) {
		this.observation = observation;
		this.loincConversionResults = new ArrayList<>();
		this.inferredConversionResults = new ArrayList<>();
	}
	
	public Observation getObservation() {
		return observation;
	}

	public void addLoincConversionResult(LoincConversionResult loincConversionResult) {
		loincConversionResults.add(loincConversionResult);
	}
	
	/**
	 * Return the conversion results specific to LOINCs found in the observation. This list is empty
	 * if no LOINCs are found.
	 * @return
	 */
	public List<LoincConversionResult> getLoincConversionResults() {
		return loincConversionResults;
	}
	
	public void addInferredConversionResult(InferredConversionResult inferredConversionResult) {
		inferredConversionResults.add(inferredConversionResult);
	}
	
	/**
	 * Return the inferred conversion results from analyzing LOINC specific HPOs. This list is empty
	 * if there were no HPO terms found or no inferences made.
	 * @return
	 */
	public List<InferredConversionResult> getInferredConversionResults() {
		return inferredConversionResults;
	}
	
}
