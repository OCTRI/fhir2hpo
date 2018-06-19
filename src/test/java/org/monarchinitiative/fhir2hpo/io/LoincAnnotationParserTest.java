package org.monarchinitiative.fhir2hpo.io;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.fhir2hpo.loinc.Loinc2HpoAnnotation;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincException;
import org.monarchinitiative.fhir2hpo.util.HpoMockUtils;
import org.monarchinitiative.phenol.ontology.data.ImmutableTermId;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class LoincAnnotationParserTest {

	private Map<LoincId, Loinc2HpoAnnotation> annotations;

	@Before
	public void setup() throws FileNotFoundException {
		InputStream annotationsResource = getClass().getClassLoader().getResourceAsStream("annotations.tsv");
		
		Map<TermId, Term> hpoTermMap = new LinkedHashMap<>();
		hpoTermMap.put(ImmutableTermId.constructWithPrefix("HP:0001873"), HpoMockUtils.mockTerm("Thrombocytopenia"));
		hpoTermMap.put(ImmutableTermId.constructWithPrefix("HP:0011873"), HpoMockUtils.mockTerm("Abnormal platelet count"));
		hpoTermMap.put(ImmutableTermId.constructWithPrefix("HP:0001894"), HpoMockUtils.mockTerm("Thrombocytosis"));
		hpoTermMap.put(ImmutableTermId.constructWithPrefix("HP:0001943"), HpoMockUtils.mockTerm("Hypoglycemia"));
		hpoTermMap.put(ImmutableTermId.constructWithPrefix("HP:0011015"), HpoMockUtils.mockTerm("Abnormality of blood glucose concentration"));
		hpoTermMap.put(ImmutableTermId.constructWithPrefix("HP:0003074"), HpoMockUtils.mockTerm("Hyperglycemia"));
		hpoTermMap.put(ImmutableTermId.constructWithPrefix("HP:0003541"), HpoMockUtils.mockTerm("Urinary glycosaminoglycan excretion"));
		annotations = LoincAnnotationParser.parse(annotationsResource, hpoTermMap);
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