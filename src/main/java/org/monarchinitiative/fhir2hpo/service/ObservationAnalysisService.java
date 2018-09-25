package org.monarchinitiative.fhir2hpo.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.Observation;
import org.monarchinitiative.fhir2hpo.fhir.util.ObservationUtil;
import org.monarchinitiative.fhir2hpo.hpo.InferredConversionResult;
import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;
import org.monarchinitiative.fhir2hpo.hpo.LoincConversionResult;
import org.monarchinitiative.fhir2hpo.hpo.ObservationConversionResult;
import org.monarchinitiative.fhir2hpo.loinc.Loinc2HpoAnnotation;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincNotAnnotatedException;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObservationAnalysisService {

	@Autowired
	AnnotationService annotationService;

	@Autowired
	HpoInferenceService hpoAugmentationService;
	
	int i = 1;
	
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
		
		result = augmentConversionResults(result);
			
		return result;
	}

	/**
	 * Augment the conversion results using rules that define relationships between HPO Terms
	 * @param result
	 * @return
	 */
	private ObservationConversionResult augmentConversionResults(ObservationConversionResult result) {
		List<HpoTermWithNegation> hpoTerms = result.getLoincConversionResults().stream().flatMap(it -> it.getHpoTerms().stream()).collect(Collectors.toList());
		HpoTermWithNegation elevatedCreatinine = new HpoTermWithNegation(TermId.constructWithPrefix("HP:0003259"), false);
		HpoTermWithNegation hyperglycemia = new HpoTermWithNegation(TermId.constructWithPrefix("HP:0003074"), false);
		HpoTermWithNegation postprandialHyperglycemia = new HpoTermWithNegation(TermId.constructWithPrefix("HP:0011998"), false);
		HpoTermWithNegation diabetes = new HpoTermWithNegation(TermId.constructWithPrefix("HP:0000819"), false);

		if (i==1) {
			hpoTerms.add(elevatedCreatinine);
			hpoTerms.add(postprandialHyperglycemia);
			i++;
		}
		
		if (hpoTerms.size() > 1) {
			List<HpoTermWithNegation> otherTerms = hpoAugmentationService.getInferredConversionResults(hpoTerms);
			if (!otherTerms.isEmpty()) {
				System.out.println(otherTerms);
				result.addInferredConversionResult(new InferredConversionResult(otherTerms.get(0)));
			}
		}
		
		return result;
	}

}
