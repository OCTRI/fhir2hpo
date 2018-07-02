package org.monarchinitiative.fhir2hpo.service;

import org.hl7.fhir.dstu3.model.Observation;
import org.monarchinitiative.fhir2hpo.fhir.util.ObservationUtil;
import org.monarchinitiative.fhir2hpo.hpo.HpoConversionResult;
import org.monarchinitiative.fhir2hpo.loinc.Loinc2HpoAnnotation;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObservationAnalysisService {

	@Autowired
	AnnotationService annotationService;

	public HpoConversionResult analyzeObservation(Observation observation) {
		
		HpoConversionResult result = new HpoConversionResult(observation);
		try {
			
			LoincId loincId = ObservationUtil.getLoincIdOfObservation(observation);
			Loinc2HpoAnnotation annotation = annotationService.getAnnotations(loincId);
			result = annotation.convert(observation);
			
		} catch (Exception e) {
			result.setException(e);
		}

		return result;
	}

}
