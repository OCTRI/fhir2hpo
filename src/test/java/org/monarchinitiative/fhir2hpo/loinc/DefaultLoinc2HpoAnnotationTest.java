package org.monarchinitiative.fhir2hpo.loinc;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Observation;
import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.fhir2hpo.codesystems.Loinc2HpoCodedValue;
import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;
import org.monarchinitiative.fhir2hpo.loinc.exception.ConflictingInternalCodesException;
import org.monarchinitiative.fhir2hpo.loinc.exception.MalformedLoincCodeException;
import org.monarchinitiative.fhir2hpo.loinc.exception.UnmappedCodeableConceptException;
import org.monarchinitiative.fhir2hpo.loinc.exception.UnmappedInternalCodeException;
import org.monarchinitiative.fhir2hpo.util.FhirMockUtils;
import org.monarchinitiative.fhir2hpo.util.HpoMockUtils;

public class DefaultLoinc2HpoAnnotationTest {
	
	private DefaultLoinc2HpoAnnotation annotation;
	private Observation observationWithLowInterpretation;
	
	@Before
	public void setup() throws MalformedLoincCodeException {
		
		LoincId loincId = new LoincId("15074-8");
		annotation = new DefaultLoinc2HpoAnnotation.Builder().setLoincId(loincId).setLoincScale(LoincScale.Qn)
			.addMapping(Loinc2HpoCodedValue.L, HpoMockUtils.mockHpoTermWithNegation("Hypoglycemia", false))
			.addMapping(Loinc2HpoCodedValue.N, HpoMockUtils.mockHpoTermWithNegation("Abnormality of blood glucose concentration", true))
			.addMapping(Loinc2HpoCodedValue.H, HpoMockUtils.mockHpoTermWithNegation("Hyperglycemia", false)).build();

		List<Coding> codings = new ArrayList<>();
		codings.add(FhirMockUtils.mockCoding("http://hl7.org/fhir/v2/0078", "LL"));
		CodeableConcept codeableConcept = FhirMockUtils.mockCodeableConcept(codings);
		observationWithLowInterpretation = new Observation();
		observationWithLowInterpretation.setInterpretation(codeableConcept);
}
	
	@Test
	public void testObservationWithInterpretation() throws UnmappedInternalCodeException, UnmappedCodeableConceptException, ConflictingInternalCodesException {
		HpoTermWithNegation low = annotation.convert(observationWithLowInterpretation);
		assertEquals("Expected code 'LL' to be mapped to Hypoglycemia.", "Hypoglycemia", low.getHpoTerm().getName());
		assertEquals("Expected Hypoglycemia not to be negated.", false, low.isNegated());
	}

    @Test(expected = UnmappedInternalCodeException.class)
	public void testUnmappedCode() throws UnmappedInternalCodeException, MalformedLoincCodeException, UnmappedCodeableConceptException, ConflictingInternalCodesException {
		// The annotations do not provide a "Low" mapping
    	LoincId loincId = new LoincId("15074-8");
		annotation = new DefaultLoinc2HpoAnnotation.Builder().setLoincId(loincId).setLoincScale(LoincScale.Qn)
			.addMapping(Loinc2HpoCodedValue.N, HpoMockUtils.mockHpoTermWithNegation("Abnormality of blood glucose concentration", true))
			.addMapping(Loinc2HpoCodedValue.H, HpoMockUtils.mockHpoTermWithNegation("Hyperglycemia", false)).build();
		annotation.convert(observationWithLowInterpretation);
    }

}
