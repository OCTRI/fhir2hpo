package org.monarchinitiative.fhir2hpo.loinc;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;
import org.monarchinitiative.fhir2hpo.loinc.exception.MalformedLoincCodeException;
import org.monarchinitiative.fhir2hpo.loinc.exception.UnmappedInternalCodeException;
import org.monarchinitiative.fhir2hpo.util.HpoMockUtils;

public class Loinc2HpoAnnotationTest {
	
	Loinc2HpoAnnotation annotation = null;
	
	@Before
	public void setup() throws MalformedLoincCodeException {
		
		LoincId loincId = new LoincId("15074-8");
		annotation = new Loinc2HpoAnnotation.Builder().setLoincId(loincId).setLoincScale(LoincScale.Qn)
			.addMapping("L", HpoMockUtils.mockHpoTermWithNegation("Hypoglycemia", false))
			.addMapping("N", HpoMockUtils.mockHpoTermWithNegation("Abnormality of blood glucose concentration", true))
			.addMapping("H", HpoMockUtils.mockHpoTermWithNegation("Hyperglycemia", false)).build();
	}
	
	@Test
	public void testMappedCode() throws UnmappedInternalCodeException {
		HpoTermWithNegation low = annotation.getHpoTermForInternalCode("L");
		assertEquals("Expected code 'L' to be mapped to Hypoglycemia.", "Hypoglycemia", low.getHpoTerm().getName());
		assertEquals("Expected Hypoglycemia not to be negated.", false, low.isNegated());
	}

    @Test(expected = UnmappedInternalCodeException.class)
	public void testUnmappedCode() throws UnmappedInternalCodeException {
		annotation.getHpoTermForInternalCode("X").getHpoTerm().getName();
	}

}
