package org.monarchinitiative.fhir2hpo.service;

import java.util.Set;

import org.hl7.fhir.dstu3.model.Observation;
import org.monarchinitiative.fhir2hpo.fhir.util.ObservationLoincInfo;
import org.monarchinitiative.fhir2hpo.fhir.util.ObservationUtil;
import org.monarchinitiative.fhir2hpo.hpo.LoincConversionResult;
import org.monarchinitiative.fhir2hpo.hpo.ObservationConversionResult;
import org.monarchinitiative.fhir2hpo.loinc.Loinc2HpoAnnotation;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincNotAnnotatedException;
import org.monarchinitiative.fhir2hpo.loinc.exception.MismatchedLoincIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObservationAnalysisService {

	@Autowired
	AnnotationService annotationService;

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
				try {
					// Save the ObservationLoincInfo and set an exception on the result
					ObservationLoincInfo loincInfo = new ObservationLoincInfo(loincId, observation);
					loincResult = new LoincConversionResult(loincInfo);
					loincResult.setException(e);
				} catch (MismatchedLoincIdException ex) {
					// We've already established that the Observation contains the LOINC, so this can't happen
				}
			}
			result.addLoincConversionResult(loincResult);
		}
			
		return result;
	}

}
