package org.monarchinitiative.fhir2hpo.fhir.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.Observation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincException;
import org.monarchinitiative.fhir2hpo.util.FhirParseUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class ObservationUtilTest {

	@Test
	public void testNoLoincId() {
		Observation observation = FhirParseUtils.getObservation("fhir/observation/noLoinc.json");
		Set<LoincId> loincs = ObservationUtil.getAllLoincIdsOfObservation(observation);
		assertTrue("Expect no LOINCs found", loincs.isEmpty());
	}

	@Test
	public void testGetCodeSectionLoincIdsOfObservation() throws LoincException {
		Observation observation = FhirParseUtils.getObservation("fhir/observation/glucoseHigh.json");
		LoincId loincId = ObservationUtil.getCodeSectionLoincIdsOfObservation(observation).iterator().next();
		assertEquals("Expected Loinc Id of ", "15074-8", loincId.getCode());
	}

	@Test
	public void testGetComponentLoincIdsOfObservation() throws LoincException {
		Observation observation = FhirParseUtils.getObservation("fhir/observation/bloodPressureComponents.json");
		Set<LoincId> loincs = ObservationUtil.getComponentLoincIdsOfObservation(observation).keySet();
		assertEquals("Expected 2 components", 2, loincs.size());
		assertTrue("Expected a loinc for systolic", loincs.contains(new LoincId("8480-6")));
		assertTrue("Expected a loinc for diastolic", loincs.contains(new LoincId("8462-4")));
	}
	
	@Test
	public void testGetDescriptionOfCodeableConcept() {
		CodeableConcept code = new CodeableConcept();
		code.setText("Text");
		
		assertEquals("The description is the text of the CodeableConcept", "Text", 
			ObservationUtil.getDescriptionOfCodeableConcept(code));
		
		Coding coding = new Coding();
		coding.setDisplay("Display");
		code.addCoding(coding);
		assertEquals("Text is the preferred description of the CodeableConcept", "Text",
			ObservationUtil.getDescriptionOfCodeableConcept(code));
		
		code.setText(null);
		assertEquals("Display is used if text does not exist", "Display",
			ObservationUtil.getDescriptionOfCodeableConcept(code));

		coding = new Coding();
		coding.setDisplay("Display2");
		code.addCoding(coding);		
		assertEquals("Only the first display is returned", "Display",
			ObservationUtil.getDescriptionOfCodeableConcept(code));
		
		code.setCoding(null);
		assertNull("Null is returned if no text or display exists", 
			ObservationUtil.getDescriptionOfCodeableConcept(code));		
	}

}
