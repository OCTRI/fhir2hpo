package org.monarchinitiative.fhir2hpo.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.fhir2hpo.loinc.DefaultLoinc2HpoAnnotation;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.exception.MalformedLoincCodeException;
import org.monarchinitiative.fhir2hpo.util.HpoMockUtils;
import org.monarchinitiative.phenol.ontology.data.ImmutableTermId;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class LoincAnnotationParserTest {

	private Map<LoincId, DefaultLoinc2HpoAnnotation> annotations;

	@Before
	public void setup() throws FileNotFoundException {
		File annotationFile = new File(getClass().getClassLoader().getResource("annotations.tsv").getFile());
		
		Map<TermId, Term> hpoTermMap = new LinkedHashMap<>();
		hpoTermMap.put(ImmutableTermId.constructWithPrefix("HP:0001873"), HpoMockUtils.mockTerm("Thrombocytopenia"));
		hpoTermMap.put(ImmutableTermId.constructWithPrefix("HP:0011873"), HpoMockUtils.mockTerm("Abnormal platelet count"));
		hpoTermMap.put(ImmutableTermId.constructWithPrefix("HP:0001894"), HpoMockUtils.mockTerm("Thrombocytosis"));
		hpoTermMap.put(ImmutableTermId.constructWithPrefix("HP:0001943"), HpoMockUtils.mockTerm("Hypoglycemia"));
		hpoTermMap.put(ImmutableTermId.constructWithPrefix("HP:0011015"), HpoMockUtils.mockTerm("Abnormality of blood glucose concentration"));
		hpoTermMap.put(ImmutableTermId.constructWithPrefix("HP:0003074"), HpoMockUtils.mockTerm("Hyperglycemia"));
		annotations = LoincAnnotationParser.parse(annotationFile, hpoTermMap);
	}
	
	@Test
	public void testAnnotationsParsed() throws MalformedLoincCodeException {

		LoincId loincId = new LoincId("777-3");
		DefaultLoinc2HpoAnnotation annotation = annotations.get(loincId);
		assertNotNull("Annotation exists for LOINC 777-3", annotation);
		
		loincId = new LoincId("15074-8");
		annotation = annotations.get(loincId);
		assertNotNull("Annotation exists for LOINC 15074-8", annotation);
		
		loincId = new LoincId("0-0");
		annotation = annotations.get(loincId);
		assertNull("No Annotation exists for LOINC 0-0", annotation);
		
	}
}
