package org.monarchinitiative.fhir2hpo.loinc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.fhir2hpo.codesystems.HpoEncodedValue;
import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;
import org.monarchinitiative.fhir2hpo.hpo.LoincConversionResult;
import org.monarchinitiative.fhir2hpo.hpo.MethodConversionResult;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincException;
import org.monarchinitiative.fhir2hpo.loinc.exception.MismatchedLoincIdException;
import org.monarchinitiative.fhir2hpo.loinc.exception.MissingInterpretationException;
import org.monarchinitiative.fhir2hpo.loinc.exception.MissingValueQuantityException;
import org.monarchinitiative.fhir2hpo.loinc.exception.ReferenceRangeNotFoundException;
import org.monarchinitiative.fhir2hpo.loinc.exception.UnmappedInternalCodeException;
import org.monarchinitiative.fhir2hpo.util.FhirParseUtils;
import org.monarchinitiative.fhir2hpo.util.HpoMockUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class DefaultLoinc2HpoAnnotationTest {

	private final static String GLUCOSE_LOINC = "15074-8";
	private final static HpoTermWithNegation HYPOGLYCEMIA = HpoMockUtils.getHpoTermWithNegation("HP:0001943", false);
	private final static HpoTermWithNegation NOT_ABNORMAL_BLOOD_GLUCOSE = HpoMockUtils
		.getHpoTermWithNegation("HP:0011015", true);
	private final static HpoTermWithNegation HYPERGLYCEMIA = HpoMockUtils.getHpoTermWithNegation("HP:0003074", false);
	private final static String BILIRUBIN_LOINC = "5770-3";
	private final static HpoTermWithNegation BILIRUNIURIA = HpoMockUtils.getHpoTermWithNegation("HP:0031811", false);
	private final static HpoTermWithNegation NOT_BILIRUNIURIA = HpoMockUtils
		.getHpoTermWithNegation("HP:0031811", true);

	// An annotation for a quantitative LOINC
	private DefaultLoinc2HpoAnnotation glucoseAnnotation;
	// An annotation for an ordinal LOINC
	private DefaultLoinc2HpoAnnotation bilirubinAnnotation;

	@Before
	public void setup() throws LoincException {

		LoincId loincId = new LoincId(GLUCOSE_LOINC);
		glucoseAnnotation = new DefaultLoinc2HpoAnnotation.Builder().setLoincId(loincId).setLoincScale(LoincScale.Qn)
			.addMapping(HpoEncodedValue.LOW, HYPOGLYCEMIA)
			.addMapping(HpoEncodedValue.NORMAL, NOT_ABNORMAL_BLOOD_GLUCOSE)
			.addMapping(HpoEncodedValue.HIGH, HYPERGLYCEMIA)
			.build();

		loincId = new LoincId(BILIRUBIN_LOINC);
		bilirubinAnnotation = new DefaultLoinc2HpoAnnotation.Builder().setLoincId(loincId).setLoincScale(LoincScale.Ord)
			.addMapping(new HpoEncodedValue(HpoEncodedValue.FHIR_SYSTEM, "POS"), BILIRUNIURIA)
			.addMapping(new HpoEncodedValue(HpoEncodedValue.FHIR_SYSTEM, "NEG"), NOT_BILIRUNIURIA)
			.build();

	}

	@Test
	public void testHpoTermsPopulated() {
		assertTrue(glucoseAnnotation.getHpoTerms().contains(HYPOGLYCEMIA));
		assertTrue(glucoseAnnotation.getHpoTerms().contains(NOT_ABNORMAL_BLOOD_GLUCOSE));
		assertTrue(glucoseAnnotation.getHpoTerms().contains(HYPERGLYCEMIA));
	}

	@Test
	public void testObservationWithoutLoinc() {
		LoincConversionResult result = glucoseAnnotation
			.convert(FhirParseUtils.getObservation("fhir/observation/noLoinc.json"));
		assertTrue("The result has an exception", result.hasException());
		assertEquals("Should not be able to convert observation without LoincId", MismatchedLoincIdException.class,
			result.getException().getClass());
	}

	public void testObservationWithAdditionalLoinc() throws LoincException {
		LoincConversionResult result = glucoseAnnotation
			.convert(FhirParseUtils.getObservation("fhir/observation/glucoseHighMultipleLoincs.json"));
		assertTrue("The conversion was successful", result.hasSuccess());
		assertEquals("The LoincId for the annotation is recorded", new LoincId(GLUCOSE_LOINC), result.getLoincId());
	}

	@Test
	public void testMismatchedLoincId() throws LoincException {
		// Tie the annotation to the code "0-0"
		LoincId loincId = new LoincId("0-0");
		glucoseAnnotation = new DefaultLoinc2HpoAnnotation.Builder().setLoincId(loincId).setLoincScale(LoincScale.Qn)
			.addMapping(HpoEncodedValue.LOW, HYPOGLYCEMIA)
			.addMapping(HpoEncodedValue.NORMAL, NOT_ABNORMAL_BLOOD_GLUCOSE)
			.addMapping(HpoEncodedValue.HIGH, HYPERGLYCEMIA)
			.build();

		// Try to convert observation with the code 15074-8
		LoincConversionResult result = glucoseAnnotation
			.convert(FhirParseUtils.getObservation("fhir/observation/glucoseHigh.json"));
		assertTrue("The result has an exception", result.hasException());
		assertEquals("Should not be able to convert with mismatched LoincId", MismatchedLoincIdException.class,
			result.getException().getClass());
	}

	@Test
	public void testObservationWithInterpretation() {

		LoincConversionResult result = glucoseAnnotation
			.convert(FhirParseUtils.getObservation("fhir/observation/glucoseHigh.json"));
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

		LoincConversionResult result = glucoseAnnotation.convert(
			FhirParseUtils.getObservation("fhir/observation/glucoseHighNoInterpretation.json"));
		MethodConversionResult interpretationResult = result.getMethodResults().get("Interpretation");
		assertTrue("The interpretation result has an exception", interpretationResult.hasException());
		assertEquals("Expected a missing interpretation exception", MissingInterpretationException.class,
			interpretationResult.getException().getClass());
	}

	@Test
	public void testUnmappedInterpretationCode() throws LoincException {

		// The annotations do not provide a "High" mapping
		LoincId loincId = new LoincId(GLUCOSE_LOINC);
		glucoseAnnotation = new DefaultLoinc2HpoAnnotation.Builder().setLoincId(loincId).setLoincScale(LoincScale.Qn)
			.addMapping(HpoEncodedValue.LOW, HYPOGLYCEMIA)
			.addMapping(HpoEncodedValue.NORMAL, NOT_ABNORMAL_BLOOD_GLUCOSE)
			.build();

		LoincConversionResult result = glucoseAnnotation
			.convert(FhirParseUtils.getObservation("fhir/observation/glucoseHigh.json"));
		MethodConversionResult interpretationResult = result.getMethodResults().get("Interpretation");
		assertTrue("The interpretation result has an exception", interpretationResult.hasException());
		assertEquals("Expected an unmapped interpretation code exception", UnmappedInternalCodeException.class,
			interpretationResult.getException().getClass());
	}

	@Test
	public void testObservationWithValueQuantity() {

		LoincConversionResult result = glucoseAnnotation
			.convert(FhirParseUtils.getObservation("fhir/observation/glucoseHigh.json"));
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

		LoincConversionResult result = glucoseAnnotation.convert(
			FhirParseUtils.getObservation("fhir/observation/glucoseHighNoValueQuantity.json"));
		MethodConversionResult valueQuantityResult = result.getMethodResults().get("ValueQuantity");
		assertTrue("The value quantity result has an exception", valueQuantityResult.hasException());
		assertEquals("Expected a missing value quantity exception", MissingValueQuantityException.class,
			valueQuantityResult.getException().getClass());
	}

	@Test
	public void testObservationWithNoReferenceRange() {

		LoincConversionResult result = glucoseAnnotation.convert(
			FhirParseUtils.getObservation("fhir/observation/glucoseHighNoReferenceRange.json"));
		MethodConversionResult valueQuantityResult = result.getMethodResults().get("ValueQuantity");
		assertTrue("The value quantity result has an exception", valueQuantityResult.hasException());
		assertEquals("Expected a reference range not found exception", ReferenceRangeNotFoundException.class,
			valueQuantityResult.getException().getClass());
	}

	@Test
	public void testObservationWithValueString() {
		LoincConversionResult result = bilirubinAnnotation.convert(FhirParseUtils.getObservation("fhir/observation/bilirubinNegative.json"));
		MethodConversionResult valueStringResult = result.getMethodResults().get("ValueString");
		HpoTermWithNegation term = valueStringResult.getTerm();
		assertEquals("Expected string 'Negative' to be mapped to Bilirubinuria.", "HP:0031811",
			term.getHpoTermId().getIdWithPrefix());
		assertEquals("Expected Bilirubinuria to be negated.", true, term.isNegated());
		assertEquals("Term can also be retrieved from main result", result.getHpoTerms().iterator().next(), term);
	}
	
	@Test
	public void testObservationWithComponentInterpretation() {
		LoincConversionResult result = glucoseAnnotation
			.convert(FhirParseUtils.getObservation("fhir/observation/glucoseHighComponentLoinc.json"));
		assertTrue("The result succeeded", result.hasSuccess());
		MethodConversionResult interpretationResult = result.getMethodResults().get("Interpretation");
		HpoTermWithNegation term = interpretationResult.getTerm();
		assertEquals("Expected code 'H' to be mapped to Hyperglycemia.", HYPERGLYCEMIA.getHpoTermId(),
			term.getHpoTermId());
		assertEquals("Expected Hyperglycemia not to be negated.", false, term.isNegated());
		assertEquals("Term can also be retrieved from main result", result.getHpoTerms().iterator().next(), term);		
	}
	
	@Test
	public void testObservationWithComponentValueQuantity() {

		LoincConversionResult result = glucoseAnnotation
			.convert(FhirParseUtils.getObservation("fhir/observation/glucoseHighComponentLoinc.json"));
		assertTrue("The result succeeded", result.hasSuccess());
		MethodConversionResult valueQuantityResult = result.getMethodResults().get("ValueQuantity");
		HpoTermWithNegation term = valueQuantityResult.getTerm();
		assertEquals("Expected code 'H' to be mapped to Hyperglycemia.", "HP:0003074",
			term.getHpoTermId().getIdWithPrefix());
		assertEquals("Expected Hyperglycemia not to be negated.", false, term.isNegated());
		assertEquals("Term can also be retrieved from main result", result.getHpoTerms().iterator().next(), term);
	}

	@Test
	public void testObservationWithComponentValueString() {
		LoincConversionResult result = bilirubinAnnotation.convert(FhirParseUtils.getObservation("fhir/observation/bilirubinNegativeComponentLoinc.json"));
		MethodConversionResult valueStringResult = result.getMethodResults().get("ValueString");
		HpoTermWithNegation term = valueStringResult.getTerm();
		assertEquals("Expected string 'Negative' to be mapped to Bilirubinuria.", "HP:0031811",
			term.getHpoTermId().getIdWithPrefix());
		assertEquals("Expected Bilirubinuria to be negated.", true, term.isNegated());
		assertEquals("Term can also be retrieved from main result", result.getHpoTerms().iterator().next(), term);
	}
	


}
