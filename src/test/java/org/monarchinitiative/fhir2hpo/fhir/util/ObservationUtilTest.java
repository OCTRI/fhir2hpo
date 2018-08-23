package org.monarchinitiative.fhir2hpo.fhir.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.hl7.fhir.dstu3.model.Observation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.fhir2hpo.config.FhirConfiguration;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincException;
import org.monarchinitiative.fhir2hpo.util.FhirParseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import ca.uhn.fhir.context.FhirContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = FhirConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class ObservationUtilTest {

	@Autowired
	FhirContext fhirContext;

	@Test
	public void testNoLoincId() {
		Observation observation = FhirParseUtils.getObservation(fhirContext, "fhir/observation/noLoinc.json");
		Set<LoincId> loincs = ObservationUtil.getAllLoincIdsOfObservation(observation);
		assertTrue("Expect no LOINCs found", loincs.isEmpty());
	}

	@Test
	public void testGetCodeSectionLoincIdsOfObservation() throws LoincException {
		Observation observation = FhirParseUtils.getObservation(fhirContext, "fhir/observation/glucoseHigh.json");
		LoincId loincId = ObservationUtil.getCodeSectionLoincIdsOfObservation(observation).iterator().next();
		assertEquals("Expected Loinc Id of ", "15074-8", loincId.getCode());
	}

	@Test
	public void testGetComponentLoincIdsOfObservation() throws LoincException {
		Observation observation = FhirParseUtils.getObservation(fhirContext, "fhir/observation/bloodPressureComponents.json");
		Set<LoincId> loincs = ObservationUtil.getComponentLoincIdsOfObservation(observation).keySet();
		assertEquals("Expected 2 components", 2, loincs.size());
		assertTrue("Expected a loinc for systolic", loincs.contains(new LoincId("8480-6")));
		assertTrue("Expected a loinc for diastolic", loincs.contains(new LoincId("8462-4")));
	}
	
	//TODO New tests for valueQuantity and valueString methods

}
