package org.monarchinitiative.fhir2hpo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlScript;
import org.apache.commons.jexl3.MapContext;
import org.monarchinitiative.fhir2hpo.hpo.AugmentationRules;
import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This service holds all the rules about how HPO Terms may be related. Pass it a set of hpo terms, and it
 * checks the rules and passes back any inferences it was able to make.
 * @author yateam
 *
 */
@Service
public class HpoAugmentationService {
	
	List<AugmentationRule> rules = new ArrayList<>();
	
	private class AugmentationRule {
		private JexlContext context;
		private JexlScript script;
		
		AugmentationRule(JexlScript script, JexlContext context) {
			this.context = context;
			this.script = script;
		}
		
		private Object evaluate(List<HpoTermWithNegation> terms) {
			context.set("L", terms);
			return script.execute(context);
		}
	}
	
	@Autowired
	public HpoAugmentationService(HpoService hpoService) {
		HpoTermWithNegation elevatedCreatinine = new HpoTermWithNegation(TermId.constructWithPrefix("HP:0003259"), false);
		HpoTermWithNegation hyperglycemia = new HpoTermWithNegation(TermId.constructWithPrefix("HP:0003074"), false);
		HpoTermWithNegation diabetes = new HpoTermWithNegation(TermId.constructWithPrefix("HP:0000819"), false);
		JexlContext jc = new MapContext();
		jc.set("L1", hpoService.getChildren(elevatedCreatinine));
		jc.set("L2", hpoService.getChildren(hyperglycemia));
		jc.set("H3", diabetes);
		
		rules.add(new AugmentationRule(AugmentationRules.AND_WITH_DESCENDANTS, jc));		
	}
	
	public List<HpoTermWithNegation> getAugmentedConversionResults(List<HpoTermWithNegation> terms) {
		return rules.stream().map(rule -> {
			Object output = rule.evaluate(terms);
			if (output != null) {
				return (HpoTermWithNegation) output;
			}
			return null;
		}).filter(it -> it!= null).collect(Collectors.toList());
	}

}
