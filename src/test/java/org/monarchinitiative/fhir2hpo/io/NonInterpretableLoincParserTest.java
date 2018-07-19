package org.monarchinitiative.fhir2hpo.io;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.fhir2hpo.loinc.Loinc2HpoAnnotation;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.NonInterpretableLoincAnnotation;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincException;

public class NonInterpretableLoincParserTest {

	private Map<LoincId, Loinc2HpoAnnotation> annotations;

	@Before
	public void setup() throws FileNotFoundException {
		InputStream annotationsResource = getClass().getClassLoader().getResourceAsStream("noninterpretable-annotations.tsv");
		
		annotations = NonInterpretableLoincParser.parse(annotationsResource);
	}
	
	@Test
	public void testAnnotationsParsed() throws LoincException {

		LoincId loincId = new LoincId("1752-5");
		Loinc2HpoAnnotation annotation = annotations.get(loincId);
		assertNotNull("Annotation exists for LOINC 1752-5", annotation);
		assertTrue("Annotation is Non-Interpretable", annotation instanceof NonInterpretableLoincAnnotation);
		
	}
}
