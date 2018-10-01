package org.monarchinitiative.fhir2hpo.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;
import org.monarchinitiative.fhir2hpo.hpo.InferredConversionResult;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * Test HPO Inference rules
 * 
 * @author yateam
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HpoInferenceService.class, HpoService.class}, loader = AnnotationConfigContextLoader.class)
public class HpoInferenceServiceTest {

    @Autowired
    HpoInferenceService hpoInferenceService;
 
	//TODO: Probably better to test rules directly than go through service like this. No tests yet for and-with-descendants
    HpoTermWithNegation decreasedHemoglobin = new HpoTermWithNegation(TermId.constructWithPrefix("HP:0020062"), false);
	HpoTermWithNegation decreasedMCV = new HpoTermWithNegation(TermId.constructWithPrefix("HP:0025066"), false);
	HpoTermWithNegation microcyticAnemia = new HpoTermWithNegation(TermId.constructWithPrefix("HP:0001935"), false);
	HpoTermWithNegation normalMCV = new HpoTermWithNegation(TermId.constructWithPrefix("HP:0025065"), true);
	HpoTermWithNegation normocyticAnemia = new HpoTermWithNegation(TermId.constructWithPrefix("HP:0001897"), false);
	HpoTermWithNegation increasedMCV = new HpoTermWithNegation(TermId.constructWithPrefix("HP:0005518"), false);
	HpoTermWithNegation macrocyticAnemia = new HpoTermWithNegation(TermId.constructWithPrefix("HP:0001972"), false);

    @Test
    public void testNoInferences() {
    	List<InferredConversionResult> inferences = hpoInferenceService.getInferredConversionResults(Arrays.asList(decreasedHemoglobin, decreasedHemoglobin));
		assertEquals("There are no results", 0, inferences.size());
    }

	@Test
    public void testMicrocyticAnemia() {
    	List<InferredConversionResult> inferences = hpoInferenceService.getInferredConversionResults(Arrays.asList(decreasedHemoglobin, decreasedMCV));
		assertEquals("There is a single result", 1, inferences.size());
		assertTrue("Microcyctic anemia was inferred", inferences.get(0).getHpoTerm().equals(microcyticAnemia));
    }

	@Test
    public void testNormocyticAnemia() {
    	List<InferredConversionResult> inferences = hpoInferenceService.getInferredConversionResults(Arrays.asList(decreasedHemoglobin, normalMCV));
		assertEquals("There is a single result", 1, inferences.size());
		assertTrue("Normocyctic anemia was inferred", inferences.get(0).getHpoTerm().equals(normocyticAnemia));
    }

	@Test
    public void testMacrocyticAnemia() {
    	List<InferredConversionResult> inferences = hpoInferenceService.getInferredConversionResults(Arrays.asList(decreasedHemoglobin, increasedMCV));
		assertEquals("There is a single result", 1, inferences.size());
		assertTrue("Macrocyctic anemia was inferred", inferences.get(0).getHpoTerm().equals(macrocyticAnemia));
    }

}
