package org.monarchinitiative.fhir2hpo.io;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.fhir2hpo.loinc.Loinc2HpoAnnotation;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincException;

public class LoincAnnotationParserTest {

	private Map<LoincId, Loinc2HpoAnnotation> annotations;

	@Before
	public void setup() throws FileNotFoundException {
		InputStream annotationsResource = getClass().getClassLoader().getResourceAsStream("annotations.tsv");
		annotations = LoincAnnotationParser.parse(annotationsResource);
	}
	
	@Test
	public void testAnnotationsParsed() throws LoincException {

		LoincId loincId = new LoincId("777-3");
		Loinc2HpoAnnotation annotation = annotations.get(loincId);
		assertNotNull("Annotation exists for LOINC 777-3", annotation);
		
		loincId = new LoincId("15074-8");
		annotation = annotations.get(loincId);
		assertNotNull("Annotation exists for LOINC 15074-8", annotation);

		loincId = new LoincId("2398-6");
		annotation = annotations.get(loincId);
		assertNotNull("Annotation exists for LOINC 2398-6", annotation);

		loincId = new LoincId("0-0");
		annotation = annotations.get(loincId);
		assertNull("No Annotation exists for LOINC 0-0", annotation);
		
	}
}
