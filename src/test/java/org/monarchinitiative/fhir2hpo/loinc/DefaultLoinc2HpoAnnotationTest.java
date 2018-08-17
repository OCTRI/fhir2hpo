package org.monarchinitiative.fhir2hpo.loinc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import org.monarchinitiative.fhir2hpo.util.FhirParseUtils;
import org.monarchinitiative.fhir2hpo.util.HpoMockUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import ca.uhn.fhir.context.FhirContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = FhirConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class DefaultLoinc2HpoAnnotationTest {

	private final static String GLUCOSE_LOINC = "15074-8";
	private final static HpoTermWithNegation HYPOGLYCEMIA = HpoMockUtils.getHpoTermWithNegation("HP:0001943", false);
	private final static HpoTermWithNegation NOT_ABNORMAL_BLOOD_GLUCOSE = HpoMockUtils
		.getHpoTermWithNegation("HP:0011015", true);
	private final static HpoTermWithNegation HYPERGLYCEMIA = HpoMockUtils.getHpoTermWithNegation("HP:0003074", false);

	@Autowired
	FhirContext fhirContext;

	private DefaultLoinc2HpoAnnotation annotation;

	@Before
	public void setup() throws LoincException {

		LoincId loincId = new LoincId(GLUCOSE_LOINC);
		annotation = new DefaultLoinc2HpoAnnotation.Builder().setLoincId(loincId).setLoincScale(LoincScale.Qn)
			.addMapping(HpoEncodedValue.LOW, HYPOGLYCEMIA)
			.addMapping(HpoEncodedValue.NORMAL, NOT_ABNORMAL_BLOOD_GLUCOSE)
			.addMapping(HpoEncodedValue.HIGH, HYPERGLYCEMIA)
			.build();

	}

	@Test
	public void testHpoTermsPopulated() {
		assertTrue(annotation.getHpoTerms().contains(HYPOGLYCEMIA));
		assertTrue(annotation.getHpoTerms().contains(NOT_ABNORMAL_BLOOD_GLUCOSE));
		assertTrue(annotation.getHpoTerms().contains(HYPERGLYCEMIA));
	}

	@Test
	public void testObservationWithoutLoinc() {
		HpoConversionResult result = annotation
			.convert(FhirParseUtils.getObservation(fhirContext, "fhir/observation/noLoinc.json"));
		assertTrue("The result has an exception", result.hasException());
		assertEquals("Should not be able to convert observation without LoincId", LoincCodeNotFoundException.class,
			result.getException().getClass());
	}

	public void testObservationWithAdditionalLoinc() throws LoincException {
		HpoConversionResult result = annotation
			.convert(FhirParseUtils.getObservation(fhirContext, "fhir/observation/glucoseHighMultipleLoincs.json"));
		assertTrue("The conversion was successful", result.hasSuccess());
		assertEquals("The LoincId for the annotation is recorded", new LoincId(GLUCOSE_LOINC), result.getLoincId());
	}

	@Test
	public void testMismatchedLoincId() throws LoincException {
		// Tie the annotation to the code "0-0"
		LoincId loincId = new LoincId("0-0");
		annotation = new DefaultLoinc2HpoAnnotation.Builder().setLoincId(loincId).setLoincScale(LoincScale.Qn)
			.addMapping(HpoEncodedValue.LOW, HYPOGLYCEMIA)
			.addMapping(HpoEncodedValue.NORMAL, NOT_ABNORMAL_BLOOD_GLUCOSE)
			.addMapping(HpoEncodedValue.HIGH, HYPERGLYCEMIA)
			.build();

		// Try to convert observation with the code 15074-8
		HpoConversionResult result = annotation
			.convert(FhirParseUtils.getObservation(fhirContext, "fhir/observation/glucoseHigh.json"));
		assertTrue("The result has an exception", result.hasException());
		assertEquals("Should not be able to convert with mismatched LoincId", MismatchedLoincIdException.class,
			result.getException().getClass());
	}

	@Test
	public void testObservationWithInterpretation() {

		HpoConversionResult result = annotation
			.convert(FhirParseUtils.getObservation(fhirContext, "fhir/observation/glucoseHigh.json"));
		assertTrue("The result succeeded", result.hasSuccess());
		MethodConversionResult interpretationResult = result.getMethodResults().get("Interpretation");
		HpoTermWithNegation term = interpretationResult.getTerm();
		assertEquals("Expected code 'H' to be mapped to Hyperglycemia.", HYPERGLYCEMIA.getHpoTermId(),
			term.getHpoTermId());
		assertEquals("Expected Hyperglycemia not to be negated.", false, term.isNegated());
		assertEquals("Term can also be retrieved from main result", result.getHpoTerms().iterator().next(), term);
	}

	@Test
	public void testObservationWithoutInterpretation() {

		HpoConversionResult result = annotation.convert(
			FhirParseUtils.getObservation(fhirContext, "fhir/observation/glucoseHighNoInterpretation.json"));
		MethodConversionResult interpretationResult = result.getMethodResults().get("Interpretation");
		assertTrue("The interpretation result has an exception", interpretationResult.hasException());
		assertEquals("Expected a missing interpretation exception", MissingInterpretationException.class,
			interpretationResult.getException().getClass());
	}

	@Test
	public void testUnmappedInterpretationCode() throws LoincException {

		// The annotations do not provide a "High" mapping
		LoincId loincId = new LoincId(GLUCOSE_LOINC);
		annotation = new DefaultLoinc2HpoAnnotation.Builder().setLoincId(loincId).setLoincScale(LoincScale.Qn)
			.addMapping(HpoEncodedValue.LOW, HYPOGLYCEMIA)
			.addMapping(HpoEncodedValue.NORMAL, NOT_ABNORMAL_BLOOD_GLUCOSE)
			.build();

		HpoConversionResult result = annotation
			.convert(FhirParseUtils.getObservation(fhirContext, "fhir/observation/glucoseHigh.json"));
		MethodConversionResult interpretationResult = result.getMethodResults().get("Interpretation");
		assertTrue("The interpretation result has an exception", interpretationResult.hasException());
		assertEquals("Expected an unmapped interpretation code exception", UnmappedInternalCodeException.class,
			interpretationResult.getException().getClass());
	}

	@Test
	public void testObservationWithValueQuantity() {

		HpoConversionResult result = annotation
			.convert(FhirParseUtils.getObservation(fhirContext, "fhir/observation/glucoseHigh.json"));
		assertTrue("The result succeeded", result.hasSuccess());
		MethodConversionResult valueQuantityResult = result.getMethodResults().get("ValueQuantity");
		HpoTermWithNegation term = valueQuantityResult.getTerm();
		assertEquals("Expected code 'H' to be mapped to Hyperglycemia.", "HP:0003074",
			term.getHpoTermId().getIdWithPrefix());
		assertEquals("Expected Hyperglycemia not to be negated.", false, term.isNegated());
		assertEquals("Term can also be retrieved from main result", result.getHpoTerms().iterator().next(), term);
	}

	@Test
	public void testObservationWithNoValueQuantity() {

		HpoConversionResult result = annotation.convert(
			FhirParseUtils.getObservation(fhirContext, "fhir/observation/glucoseHighNoValueQuantity.json"));
		MethodConversionResult valueQuantityResult = result.getMethodResults().get("ValueQuantity");
		assertTrue("The value quantity result has an exception", valueQuantityResult.hasException());
		assertEquals("Expected a missing value quantity exception", MissingValueQuantityException.class,
			valueQuantityResult.getException().getClass());
	}

	@Test
	public void testObservationWithNoReferenceRange() {

		HpoConversionResult result = annotation.convert(
			FhirParseUtils.getObservation(fhirContext, "fhir/observation/glucoseHighNoReferenceRange.json"));
		MethodConversionResult valueQuantityResult = result.getMethodResults().get("ValueQuantity");
		assertTrue("The value quantity result has an exception", valueQuantityResult.hasException());
		assertEquals("Expected a reference range not found exception", ReferenceRangeNotFoundException.class,
			valueQuantityResult.getException().getClass());
	}

	// TODO: May want other tests for these exceptions once we have a better understanding of expected formats
	// AmbiguousReferenceRangeException

}
