package org.monarchinitiative.fhir2hpo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hl7.fhir.dstu3.model.Observation;
import org.monarchinitiative.fhir2hpo.fhir.util.ObservationUtil;
import org.monarchinitiative.fhir2hpo.hpo.HpoConversionResult;
import org.monarchinitiative.fhir2hpo.loinc.Loinc2HpoAnnotation;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincCodeNotFoundException;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincNotAnnotatedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObservationAnalysisService {

	@Autowired
	AnnotationService annotationService;

	/**
	 * Analyze the observation and return the result. An observation may have multiple LOINC in both the code and component
	 * sections, so multiple results are possible.
	 * @param observation
	 * @return
	 */
	public List<HpoConversionResult> analyzeObservation(Observation observation) {
		
		List<HpoConversionResult> results = new ArrayList<>();
		
		Set<LoincId> loincIds = ObservationUtil.getAllLoincIdsOfObservation(observation);
		if (loincIds.isEmpty()) {
			// Error getting LOINCs from Observation. Add exception and return.
			HpoConversionResult result = new HpoConversionResult(observation, null);
			result.setException(new LoincCodeNotFoundException());
			results.add(result);
			return results;
		}
		
		for (LoincId loincId : loincIds) {
			HpoConversionResult result = new HpoConversionResult(observation, loincId);
			try {
				Loinc2HpoAnnotation annotation = annotationService.getAnnotations(loincId);
				result = annotation.convert(observation);
			} catch (LoincNotAnnotatedException e) {
				result.setException(e);
			}
			results.add(result);
		}
			
		return results;
	}

}
