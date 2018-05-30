package org.monarchinitiative.fhir2hpo.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.monarchinitiative.fhir2hpo.codesystems.Loinc2HpoCodedValue;
import org.monarchinitiative.fhir2hpo.loinc.DefaultLoinc2HpoAnnotation;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.LoincScale;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincException;
import org.monarchinitiative.fhir2hpo.util.HpoMockUtils;

/**
 * This demonstrates that the service can be mocked without the resources, but doesn't have value otherwise.
 * 
 * @author yateam
 *
 */
public class AnnotationServiceTest {
	
	AnnotationService annotationService;
	
	@Before
	public void setup() throws LoincException {
		
		LoincId loincId = new LoincId("15074-8");
		DefaultLoinc2HpoAnnotation annotation = new DefaultLoinc2HpoAnnotation.Builder().setLoincId(loincId).setLoincScale(LoincScale.Qn)
			.addMapping(Loinc2HpoCodedValue.L, HpoMockUtils.mockHpoTermWithNegation("Hypoglycemia", false))
			.addMapping(Loinc2HpoCodedValue.N, HpoMockUtils.mockHpoTermWithNegation("Abnormality of blood glucose concentration", true))
			.addMapping(Loinc2HpoCodedValue.H, HpoMockUtils.mockHpoTermWithNegation("Hyperglycemia", false)).build();

		LoincId loincId2 = new LoincId("777-3");
		DefaultLoinc2HpoAnnotation annotation2 = new DefaultLoinc2HpoAnnotation.Builder().setLoincId(loincId).setLoincScale(LoincScale.Qn)
			.addMapping(Loinc2HpoCodedValue.L, HpoMockUtils.mockHpoTermWithNegation("Thrombocytopenia", false))
			.addMapping(Loinc2HpoCodedValue.N, HpoMockUtils.mockHpoTermWithNegation("Abnormal platelet count", true))
			.addMapping(Loinc2HpoCodedValue.H, HpoMockUtils.mockHpoTermWithNegation("Thrombocytosis", false)).build();

		// Mock the service to return annotations for two LOINCs
		annotationService = mock(AnnotationService.class);
		when(annotationService.getAnnotations(loincId)).thenReturn(annotation);
		when(annotationService.getAnnotations(loincId2)).thenReturn(annotation2);
	}
	
}
