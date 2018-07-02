package org.monarchinitiative.fhir2hpo.fhir.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import org.hl7.fhir.dstu3.model.Observation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.fhir2hpo.config.FhirConfiguration;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincCodeNotFoundException;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = FhirConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class ObservationUtilTest {
	
    @Autowired
	FhirContext fhirContext;

    @Test(expected = LoincCodeNotFoundException.class)
	public void testNoLoincId() throws LoincException {
    	Observation observation = getObservation("fhir/observation/noLoinc.json");	
    	ObservationUtil.getLoincIdOfObservation(observation);
	}

    @Test
	public void testGetLoincIdOfObservation() throws LoincException {
    	Observation observation = getObservation("fhir/observation/glucoseHigh.json");		
    	LoincId loincId = ObservationUtil.getLoincIdOfObservation(observation);
    	assertEquals("Expected Loinc Id of ", "15074-8", loincId.getCode());
	}

	@Test
	public void testGetComponentLoincIdsOfObservation() throws LoincException {
    	Observation observation = getObservation("fhir/observation/bloodPressureComponents.json");		
    	Set<LoincId> loincs = ObservationUtil.getComponentLoincIdsOfObservation(observation);
    	assertEquals("Expected 2 components", 2, loincs.size());
    	assertTrue("Expected a loinc for systolic", loincs.contains(new LoincId("8480-6")));
    	assertTrue("Expected a loinc for diastolic", loincs.contains(new LoincId("8462-4")));
	}

	// Parse an observation given the path to the file
	private Observation getObservation(String path) {
		IParser parser = fhirContext.newJsonParser();
		InputStream stream = this.getClass().getClassLoader().getResourceAsStream(path);
		return (Observation) parser.parseResource(new InputStreamReader(stream));
	}

}
