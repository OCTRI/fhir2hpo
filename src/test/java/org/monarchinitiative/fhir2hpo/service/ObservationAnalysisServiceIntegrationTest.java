package org.monarchinitiative.fhir2hpo.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.hl7.fhir.dstu3.model.Observation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.fhir2hpo.config.FhirConfiguration;
import org.monarchinitiative.fhir2hpo.hpo.HpoConversionResult;
import org.monarchinitiative.fhir2hpo.loinc.exception.NonInterpretableLoincException;
import org.monarchinitiative.fhir2hpo.util.FhirParseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import ca.uhn.fhir.context.FhirContext;

/**
 * This integration test is fully wired, so you can test or debug real observations.
 * 
 * @author yateam
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {FhirConfiguration.class, ObservationAnalysisService.class, AnnotationService.class}, loader = AnnotationConfigContextLoader.class)
public class ObservationAnalysisServiceIntegrationTest {

    @Autowired
	FhirContext fhirContext;

    @Autowired
    ObservationAnalysisService observationAnalysisService;
    
    @Test
    public void testAnalyzeObservation() {
    	Observation observation = FhirParseUtils.getObservation(fhirContext, "fhir/observation/glucoseHigh.json");
    	List<HpoConversionResult> results = observationAnalysisService.analyzeObservation(observation);
    	assertEquals("There is a single result", 1, results.size());
		assertTrue("The result succeeded", results.get(0).hasSuccess());
    }

    @Test
    public void testNonInterpetableObservation() {
    	Observation observation = FhirParseUtils.getObservation(fhirContext, "fhir/observation/nonInterpretableLoinc.json");
    	List<HpoConversionResult> results = observationAnalysisService.analyzeObservation(observation);
    	assertEquals("There is a single result", 1, results.size());
		assertTrue("The result failed", results.get(0).hasException());
		assertTrue("The exception indicates the LOINC is not interpretable.", results.get(0).getException() instanceof NonInterpretableLoincException);
    }
    
    @Test
    public void testPanelWithMultipleAnnotatedComponents() {
    	Observation observation = FhirParseUtils.getObservation(fhirContext, "fhir/observation/basMetab1998Panel.json");
    	List<HpoConversionResult> results = observationAnalysisService.analyzeObservation(observation);
    	assertTrue("There is a result for LOINC 2160-0", results.stream().anyMatch(it -> "2160-0".equals(it.getLoincId().getCode())));
    	assertTrue("There is a result for LOINC 2345-7", results.stream().anyMatch(it -> "2345-7".equals(it.getLoincId().getCode())));
    	assertEquals("Two results have HPO terms", 2, results.stream().filter(it -> it.hasSuccess()).count());    	
    }

}
