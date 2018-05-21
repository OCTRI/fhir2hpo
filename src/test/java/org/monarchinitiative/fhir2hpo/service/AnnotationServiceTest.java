package org.monarchinitiative.fhir2hpo.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;
import org.monarchinitiative.fhir2hpo.loinc.Loinc2HpoAnnotation;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.LoincScale;
import org.monarchinitiative.fhir2hpo.loinc.exception.MalformedLoincCodeException;
import org.monarchinitiative.fhir2hpo.loinc.exception.UnmappedInternalCodeException;
import org.monarchinitiative.fhir2hpo.util.HpoMockUtils;

public class AnnotationServiceTest {
	
	AnnotationService annotationService;
	
	@Before
	public void setup() throws MalformedLoincCodeException {
		
		LoincId loincId = new LoincId("15074-8");
		Loinc2HpoAnnotation annotation = new Loinc2HpoAnnotation.Builder().setLoincId(loincId).setLoincScale(LoincScale.Qn)
			.addMapping("L", HpoMockUtils.mockHpoTermWithNegation("Hypoglycemia", false))
			.addMapping("N", HpoMockUtils.mockHpoTermWithNegation("Abnormality of blood glucose concentration", true))
			.addMapping("H", HpoMockUtils.mockHpoTermWithNegation("Hyperglycemia", false)).build();

		LoincId loincId2 = new LoincId("777-3");
		Loinc2HpoAnnotation annotation2 = new Loinc2HpoAnnotation.Builder().setLoincId(loincId).setLoincScale(LoincScale.Qn)
			.addMapping("L", HpoMockUtils.mockHpoTermWithNegation("Thrombocytopenia", false))
			.addMapping("N", HpoMockUtils.mockHpoTermWithNegation("Abnormal platelet count", true))
			.addMapping("H", HpoMockUtils.mockHpoTermWithNegation("Thrombocytosis", false)).build();

		// Mock the service to return annotations for two LOINCs
		annotationService = mock(AnnotationService.class);
		when(annotationService.getAnnotations(loincId)).thenReturn(annotation);
		when(annotationService.getAnnotations(loincId2)).thenReturn(annotation2);
	}
	
	@Test
	public void testGetAnnotationForLoincId() throws MalformedLoincCodeException, UnmappedInternalCodeException {
		Loinc2HpoAnnotation annotation = annotationService.getAnnotations(new LoincId("777-3"));
		HpoTermWithNegation term = annotation.getHpoTermForInternalCode("N");
		assertEquals("Expected term of Abnormal Platelet Count", term.getHpoTerm().getName(), "Abnormal platelet count");
		assertEquals("Expected term to be negated.", true, term.isNegated());
	}

}
