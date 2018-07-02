package org.monarchinitiative.fhir2hpo.loinc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.hl7.fhir.dstu3.model.Observation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.fhir2hpo.codesystems.HpoEncodedValue;
import org.monarchinitiative.fhir2hpo.config.FhirConfiguration;
import org.monarchinitiative.fhir2hpo.hpo.HpoConversionResult;
import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;
import org.monarchinitiative.fhir2hpo.hpo.MethodConversionResult;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincCodeNotFoundException;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincException;
import org.monarchinitiative.fhir2hpo.loinc.exception.MismatchedLoincIdException;
import org.monarchinitiative.fhir2hpo.loinc.exception.MissingInterpretationException;
import org.monarchinitiative.fhir2hpo.loinc.exception.MissingValueQuantityException;
import org.monarchinitiative.fhir2hpo.loinc.exception.ReferenceRangeNotFoundException;
import org.monarchinitiative.fhir2hpo.loinc.exception.UnmappedInternalCodeException;
import org.monarchinitiative.fhir2hpo.util.HpoMockUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = FhirConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class DefaultLoinc2HpoAnnotationTest {

    @Autowired
	FhirContext fhirContext;

	private DefaultLoinc2HpoAnnotation annotation;

	@Before
	public void setup() throws LoincException {

		LoincId loincId = new LoincId("15074-8");
		annotation = new DefaultLoinc2HpoAnnotation.Builder().setLoincId(loincId).setLoincScale(LoincScale.Qn)
				.addMapping(HpoEncodedValue.LOW, HpoMockUtils.mockHpoTermWithNegation("Hypoglycemia", false))
				.addMapping(HpoEncodedValue.NORMAL,
						HpoMockUtils.mockHpoTermWithNegation("Abnormality of blood glucose concentration", true))
				.addMapping(HpoEncodedValue.HIGH, HpoMockUtils.mockHpoTermWithNegation("Hyperglycemia", false))
				.build();

	}
	
	@Test
	public void testObservationWithoutLoinc() {
		HpoConversionResult result = annotation.convert(getObservation("fhir/observation/noLoinc.json"));
		assertTrue("The result has an exception", result.hasException());
		assertEquals("Should not be able to convert observation without LoincId", LoincCodeNotFoundException.class, result.getException().getClass()); 
	}
	
	@Test
	public void testMismatchedLoincId() throws LoincException {
		// Tie the annotation to the code "0-0"
		LoincId loincId = new LoincId("0-0");
		annotation = new DefaultLoinc2HpoAnnotation.Builder().setLoincId(loincId).setLoincScale(LoincScale.Qn)
				.addMapping(HpoEncodedValue.LOW, HpoMockUtils.mockHpoTermWithNegation("Hypoglycemia", false))
				.addMapping(HpoEncodedValue.NORMAL,
						HpoMockUtils.mockHpoTermWithNegation("Abnormality of blood glucose concentration", true))
				.addMapping(HpoEncodedValue.HIGH, HpoMockUtils.mockHpoTermWithNegation("Hyperglycemia", false))
				.build();
		
		// Try to convert observation with the code 15074-8
		HpoConversionResult result = annotation.convert(getObservation("fhir/observation/glucoseHigh.json"));
		assertTrue("The result has an exception", result.hasException());
		assertEquals("Should not be able to convert with mismatched LoincId", MismatchedLoincIdException.class, result.getException().getClass()); 
	}

	@Test
	public void testObservationWithInterpretation() {

		HpoConversionResult result = annotation.convert(getObservation("fhir/observation/glucoseHigh.json"));
		assertTrue("The result succeeded", result.hasSuccess());
		MethodConversionResult interpretationResult = result.getMethodResults().get("Interpretation");
		HpoTermWithNegation term = interpretationResult.getTerm();
		assertEquals("Expected code 'H' to be mapped to Hyperglycemia.", "Hyperglycemia", term.getHpoTerm().getName());
		assertEquals("Expected Hyperglycemia not to be negated.", false, term.isNegated());
		assertEquals("Term can also be retrieved from main result", result.getHpoTerms().iterator().next(), term);
	}

	@Test
	public void testObservationWithoutInterpretation() {

		HpoConversionResult result = annotation.convert(getObservation("fhir/observation/glucoseHighNoInterpretation.json"));
		MethodConversionResult interpretationResult = result.getMethodResults().get("Interpretation");
		assertTrue("The interpretation result has an exception", interpretationResult.hasException());
		assertEquals("Expected a missing interpretation exception", MissingInterpretationException.class, interpretationResult.getException().getClass());
	}

	@Test
	public void testUnmappedInterpretationCode() throws LoincException {

		// The annotations do not provide a "High" mapping
		LoincId loincId = new LoincId("15074-8");
		annotation = new DefaultLoinc2HpoAnnotation.Builder().setLoincId(loincId).setLoincScale(LoincScale.Qn)
				.addMapping(HpoEncodedValue.LOW, HpoMockUtils.mockHpoTermWithNegation("Hypoglycemia", false))
				.addMapping(HpoEncodedValue.NORMAL,
						HpoMockUtils.mockHpoTermWithNegation("Abnormality of blood glucose concentration", true))
				.build();
		
		HpoConversionResult result = annotation.convert(getObservation("fhir/observation/glucoseHigh.json"));
		MethodConversionResult interpretationResult = result.getMethodResults().get("Interpretation");
		assertTrue("The interpretation result has an exception", interpretationResult.hasException());
		assertEquals("Expected an unmapped interpretation code exception", UnmappedInternalCodeException.class, interpretationResult.getException().getClass());
	}

	@Test
	public void testObservationWithValueQuantity() {

		HpoConversionResult result = annotation.convert(getObservation("fhir/observation/glucoseHigh.json"));
		assertTrue("The result succeeded", result.hasSuccess());
		MethodConversionResult valueQuantityResult = result.getMethodResults().get("ValueQuantity");
		HpoTermWithNegation term = valueQuantityResult.getTerm();
		assertEquals("Expected code 'H' to be mapped to Hyperglycemia.", "Hyperglycemia", term.getHpoTerm().getName());
		assertEquals("Expected Hyperglycemia not to be negated.", false, term.isNegated());
		assertEquals("Term can also be retrieved from main result", result.getHpoTerms().iterator().next(), term);
	}

	@Test
	public void testObservationWithNoValueQuantity() {

		HpoConversionResult result = annotation.convert(getObservation("fhir/observation/glucoseHighNoValueQuantity.json"));
		MethodConversionResult valueQuantityResult = result.getMethodResults().get("ValueQuantity");
		assertTrue("The value quantity result has an exception", valueQuantityResult.hasException());
		assertEquals("Expected a missing value quantity exception", MissingValueQuantityException.class, valueQuantityResult.getException().getClass());
	}

	@Test
	public void testObservationWithNoReferenceRange() {

		HpoConversionResult result = annotation.convert(getObservation("fhir/observation/glucoseHighNoReferenceRange.json"));
		MethodConversionResult valueQuantityResult = result.getMethodResults().get("ValueQuantity");
		assertTrue("The value quantity result has an exception", valueQuantityResult.hasException());
		assertEquals("Expected a reference range not found exception", ReferenceRangeNotFoundException.class, valueQuantityResult.getException().getClass());
	}

	// Parse an observation given the path to the file
	private Observation getObservation(String path) {

		IParser parser = fhirContext.newJsonParser();
		InputStream stream = this.getClass().getClassLoader().getResourceAsStream(path);
		return (Observation) parser.parseResource(new InputStreamReader(stream));
	}
	
	//TODO: May want other tests for these exceptions once we have a better understanding of expected formats
	// AmbiguousReferenceRangeException
	// ConflictingLoincCodesException

}
