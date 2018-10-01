package org.monarchinitiative.fhir2hpo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;
import org.monarchinitiative.fhir2hpo.hpo.InferredConversionResult;
import org.monarchinitiative.fhir2hpo.hpo.rules.AndRule;
import org.monarchinitiative.fhir2hpo.hpo.rules.HpoInferenceRule;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This service holds all the rules about how HPO Terms may be related. Pass it a set of hpo terms, and it
 * checks the rules and passes back any inferences it was able to make.
 * 
 * @author yateam
 *
 */
@Service
public class HpoInferenceService {
	
	List<HpoInferenceRule> rules = new ArrayList<>();
	
	@Autowired
	public HpoInferenceService(HpoService hpoService) {
		
		// Decreased Hemoglobin AND Decreased MCV => Mycrocytic Anemia
		HpoTermWithNegation decreasedHemoglobin = new HpoTermWithNegation(TermId.constructWithPrefix("HP:0020062"), false);
		HpoTermWithNegation decreasedMCV = new HpoTermWithNegation(TermId.constructWithPrefix("HP:0025066"), false);
		HpoTermWithNegation microcyticAnemia = new HpoTermWithNegation(TermId.constructWithPrefix("HP:0001935"), false);
		HpoInferenceRule rule = new AndRule(decreasedHemoglobin, decreasedMCV, microcyticAnemia);
		rules.add(rule);		
	
		// Decreased Hemoglobin AND Normal MCV => Normocytic Anemia
		HpoTermWithNegation normalMCV = new HpoTermWithNegation(TermId.constructWithPrefix("HP:0025065"), true);
		HpoTermWithNegation normocyticAnemia = new HpoTermWithNegation(TermId.constructWithPrefix("HP:0001897"), false);
		rule = new AndRule(decreasedHemoglobin, normalMCV, normocyticAnemia);
		rules.add(rule);
		
		// Decreased Hemoglobin AND Increased MCV => Macrocytic Anemia
		HpoTermWithNegation increasedMCV = new HpoTermWithNegation(TermId.constructWithPrefix("HP:0005518"), false);
		HpoTermWithNegation macrocyticAnemia = new HpoTermWithNegation(TermId.constructWithPrefix("HP:0001972"), false);
		rule = new AndRule(decreasedHemoglobin, increasedMCV, macrocyticAnemia);
		rules.add(rule);
		
}
	
	public List<InferredConversionResult> getInferredConversionResults(List<HpoTermWithNegation> terms) {
		return rules.stream().map(rule -> {
			HpoTermWithNegation term = rule.evaluate(terms);
			if (term != null) {
				return new InferredConversionResult(term, rule.getDescription());
			}
			return null;
		}).filter(it -> it!= null).collect(Collectors.toList());
	}

}
