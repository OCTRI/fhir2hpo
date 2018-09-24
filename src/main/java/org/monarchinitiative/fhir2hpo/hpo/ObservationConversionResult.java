package org.monarchinitiative.fhir2hpo.hpo;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.dstu3.model.Observation;

/**
 * The ObservationConversionResult consists of the original observation and a collection of LoincConversionResults
 * corresponding to each LOINC found in the observation. LOINCs can be found in both the code section and components
 * section of the observation. The list is empty if no LOINCs are found.
 * 
 * @author yateam
 *
 */
public class ObservationConversionResult {
	
	private Observation observation;
	private List<LoincConversionResult> loincConversionResults;
	private List<AugmentedConversionResult> augmentedConversionResults;
	
	public ObservationConversionResult(Observation observation) {
		this.observation = observation;
		this.loincConversionResults = new ArrayList<>();
		this.augmentedConversionResults = new ArrayList<>();
	}
	
	public Observation getObservation() {
		return observation;
	}

	public void addLoincConversionResult(LoincConversionResult loincConversionResult) {
		loincConversionResults.add(loincConversionResult);
	}
	
	public List<LoincConversionResult> getLoincConversionResults() {
		return loincConversionResults;
	}
	
	public void addAugmentedConversionResult(AugmentedConversionResult augmentedConversionResult) {
		augmentedConversionResults.add(augmentedConversionResult);
	}
	
	public List<AugmentedConversionResult> getAugmentedConversionResults() {
		return augmentedConversionResults;
	}
	
}
