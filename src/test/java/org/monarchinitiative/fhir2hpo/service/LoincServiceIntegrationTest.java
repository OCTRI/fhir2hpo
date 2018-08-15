package org.monarchinitiative.fhir2hpo.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.fhir2hpo.config.Fhir2HpoConfiguration;
import org.monarchinitiative.fhir2hpo.loinc.Loinc;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.LoincScale;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Fhir2HpoConfiguration.class, LoincService.class}, loader = AnnotationConfigContextLoader.class)
public class LoincServiceIntegrationTest {

	// TODO: Remove integration test, and just test parsing a response stored as a resource.
	@Autowired
	LoincService loincService;

    @Test
    public void testFindLoincId() throws LoincException {
    	LoincId loincId = new LoincId("4544-3"); 
    	Loinc loinc = loincService.findLoincById(loincId);
    	assertEquals("The LoindId matches", loinc.getLoincId(), loincId);
    	assertEquals("The scale is Qn", LoincScale.Qn, loinc.getLoincScale());
    	assertEquals("The test display name matches", "Hematocrit [Volume Fraction] of Blood by Automated count", loinc.getDisplayName());
    }

//    @Test
//    public void testNonExistentLoincId() throws LoincException {
//    	String result = loincService.findLoincById(new LoincId("999-9"));
//    	System.out.println(result);
//    }

}
