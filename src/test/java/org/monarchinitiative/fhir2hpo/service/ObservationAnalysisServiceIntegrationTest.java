package org.monarchinitiative.fhir2hpo.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.hl7.fhir.dstu3.model.Observation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.fhir2hpo.hpo.LoincConversionResult;
import org.monarchinitiative.fhir2hpo.loinc.exception.NonInterpretableLoincException;
import org.monarchinitiative.fhir2hpo.util.FhirParseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * This integration test is fully wired, so you can test or debug real observations.
 * 
 * @author yateam
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ObservationAnalysisService.class, AnnotationService.class, HpoInferenceService.class, HpoService.class}, loader = AnnotationConfigContextLoader.class)
public class ObservationAnalysisServiceIntegrationTest {

    @Autowired
    ObservationAnalysisService observationAnalysisService;
    
    @Test
    public void testAnalyzeObservation() {
    	Observation observation = FhirParseUtils.getObservation("fhir/observation/glucoseHigh.json");
    	List<LoincConversionResult> results = observationAnalysisService.analyzeObservation(observation).getLoincConversionResults();
    	assertEquals("There is a single result", 1, results.size());
		assertTrue("The result succeeded", results.get(0).hasSuccess());
    }

    @Test
    public void testNonInterpetableObservation() {
    	Observation observation = FhirParseUtils.getObservation("fhir/observation/nonInterpretableLoinc.json");
    	List<LoincConversionResult> results = observationAnalysisService.analyzeObservation(observation).getLoincConversionResults();
    	assertEquals("There is a single result", 1, results.size());
		assertTrue("The result failed", results.get(0).hasException());
		assertTrue("The exception indicates the LOINC is not interpretable.", results.get(0).getException() instanceof NonInterpretableLoincException);
    }
    
    @Test
    public void testPanelWithMultipleAnnotatedComponents() {
    	Observation observation = FhirParseUtils.getObservation("fhir/observation/basMetab1998Panel.json");
    	List<LoincConversionResult> results = observationAnalysisService.analyzeObservation(observation).getLoincConversionResults();
    	assertTrue("There is a result for LOINC 2160-0", results.stream().anyMatch(it -> "2160-0".equals(it.getLoincId().getCode())));
    	assertTrue("There is a result for LOINC 2345-7", results.stream().anyMatch(it -> "2345-7".equals(it.getLoincId().getCode())));
    	assertEquals("Two results have HPO terms", 2, results.stream().filter(it -> it.hasSuccess()).count());    	
    }

}
