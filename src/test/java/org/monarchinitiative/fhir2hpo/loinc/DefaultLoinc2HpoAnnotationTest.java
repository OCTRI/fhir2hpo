package org.monarchinitiative.fhir2hpo.loinc;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.exceptions.FHIRException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.fhir2hpo.codesystems.Loinc2HpoCodedValue;
import org.monarchinitiative.fhir2hpo.config.FhirConfiguration;
import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;
import org.monarchinitiative.fhir2hpo.loinc.exception.ConversionException;
import org.monarchinitiative.fhir2hpo.loinc.exception.ConversionException.ConversionExceptionType;
import org.monarchinitiative.fhir2hpo.loinc.exception.MalformedLoincCodeException;
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
	public void setup() throws MalformedLoincCodeException {

		LoincId loincId = new LoincId("15074-8");
		annotation = new DefaultLoinc2HpoAnnotation.Builder().setLoincId(loincId).setLoincScale(LoincScale.Qn)
				.addMapping(Loinc2HpoCodedValue.L, HpoMockUtils.mockHpoTermWithNegation("Hypoglycemia", false))
				.addMapping(Loinc2HpoCodedValue.N,
						HpoMockUtils.mockHpoTermWithNegation("Abnormality of blood glucose concentration", true))
				.addMapping(Loinc2HpoCodedValue.H, HpoMockUtils.mockHpoTermWithNegation("Hyperglycemia", false))
				.build();

	}

	@Test
	public void testObservationWithInterpretation() throws ConversionException, FHIRException {

		HpoTermWithNegation term = annotation.convert(getObservation("fhir/glucoseHighInterpretation.json"));
		assertEquals("Expected code 'H' to be mapped to Hyperglycemia.", "Hyperglycemia", term.getHpoTerm().getName());
		assertEquals("Expected Hyperglycemia not to be negated.", false, term.isNegated());
	}

	public void testUnmappedInterpretationCode() throws MalformedLoincCodeException, FHIRException {

		// The annotations do not provide a "High" mapping
		LoincId loincId = new LoincId("15074-8");
		annotation = new DefaultLoinc2HpoAnnotation.Builder().setLoincId(loincId).setLoincScale(LoincScale.Qn)
				.addMapping(Loinc2HpoCodedValue.L, HpoMockUtils.mockHpoTermWithNegation("Hypoglycemia", false))
				.addMapping(Loinc2HpoCodedValue.N,
						HpoMockUtils.mockHpoTermWithNegation("Abnormality of blood glucose concentration", true))
				.build();
		try {
			annotation.convert(getObservation("fhir/glucoseHighInterpretation.json"));
		} catch (ConversionException e) {
			assertEquals("Expected an unmapped interpretation code exception", ConversionExceptionType.UNMAPPED_INTERNAL_CODE, e.getType());
		}
	}

	@Test
	public void testObservationWithValueQuantity() throws ConversionException, FHIRException {

		HpoTermWithNegation term = annotation.convert(getObservation("fhir/glucoseHighValueQuantity.json"));
		assertEquals("Expected code 'H' to be mapped to Hyperglycemia.", "Hyperglycemia", term.getHpoTerm().getName());
		assertEquals("Expected Hyperglycemia not to be negated.", false, term.isNegated());
	}

	// Parse an observation given the path to the file
	private Observation getObservation(String path) {

		IParser parser = fhirContext.newJsonParser();
		InputStream stream = this.getClass().getClassLoader().getResourceAsStream(path);
		return (Observation) parser.parseResource(new InputStreamReader(stream));
	}

}
