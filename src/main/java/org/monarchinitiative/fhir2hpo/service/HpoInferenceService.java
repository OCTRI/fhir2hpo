package org.monarchinitiative.fhir2hpo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;
import org.monarchinitiative.fhir2hpo.hpo.rules.AndWithDescendantsRule;
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
		HpoTermWithNegation elevatedCreatinine = new HpoTermWithNegation(TermId.constructWithPrefix("HP:0003259"), false);
		HpoTermWithNegation hyperglycemia = new HpoTermWithNegation(TermId.constructWithPrefix("HP:0003074"), false);
		HpoTermWithNegation diabetes = new HpoTermWithNegation(TermId.constructWithPrefix("HP:0000819"), false);
		HpoInferenceRule rule = new AndWithDescendantsRule(hpoService.getChildren(elevatedCreatinine), hpoService.getChildren(hyperglycemia), diabetes);
		rules.add(rule);		
	}
	
	public List<HpoTermWithNegation> getInferredConversionResults(List<HpoTermWithNegation> terms) {
		return rules.stream().map(rule -> rule.evaluate(terms)).filter(it -> it!= null).collect(Collectors.toList());
	}

}
