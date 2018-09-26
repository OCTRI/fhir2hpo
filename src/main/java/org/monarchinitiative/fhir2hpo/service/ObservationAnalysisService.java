package org.monarchinitiative.fhir2hpo.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.Observation;
import org.monarchinitiative.fhir2hpo.fhir.util.ObservationUtil;
import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;
import org.monarchinitiative.fhir2hpo.hpo.LoincConversionResult;
import org.monarchinitiative.fhir2hpo.hpo.ObservationConversionResult;
import org.monarchinitiative.fhir2hpo.loinc.Loinc2HpoAnnotation;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincNotAnnotatedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObservationAnalysisService {

	@Autowired
	AnnotationService annotationService;

	@Autowired
	HpoInferenceService hpoInferenceService;
	
	/**
	 * Analyze the observation and return the result.
	 * @param observation
	 * @return the result encompassing any LOINCs encountered in the observation
	 */
	public ObservationConversionResult analyzeObservation(Observation observation) {
		
		ObservationConversionResult result = new ObservationConversionResult(observation);
		
		Set<LoincId> loincIds = ObservationUtil.getAllLoincIdsOfObservation(observation);
		for (LoincId loincId : loincIds) {
			LoincConversionResult loincResult = null;
			try {
				Loinc2HpoAnnotation annotation = annotationService.getAnnotations(loincId);
				loincResult = annotation.convert(observation);
			} catch (LoincNotAnnotatedException e) {
				loincResult = new LoincConversionResult(loincId);
				loincResult.setException(e);
			}
			result.addLoincConversionResult(loincResult);
		}
		
		// See if any additional results can be inferred
		List<HpoTermWithNegation> hpoTerms = result.getLoincConversionResults().stream().flatMap(it -> it.getHpoTerms().stream()).collect(Collectors.toList());
		if (hpoTerms.size() > 1) {
			result.setInferredConversionResults(hpoInferenceService.getInferredConversionResults(hpoTerms));
		}
		
		return result;
	}

}
