package org.monarchinitiative.fhir2hpo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.fhir2hpo.loinc.DefaultLoinc2HpoAnnotation;
import org.monarchinitiative.fhir2hpo.loinc.Loinc2HpoAnnotation;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincException;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincNotAnnotatedException;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermPrefix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AnnotationService.class, HpoService.class}, loader = AnnotationConfigContextLoader.class)
public class AnnotationServiceIntegrationTest {
	
	@Autowired
	AnnotationService annotationService;
	
	@Autowired
	HpoService hpoService;
	
	@Test
	public void reportLoincs() throws LoincException, IOException {
		
		List<LoincId> list = new ArrayList<>();

//		//Use Richard's list of LOINCs for EDS patients. Provide a spreadsheet that reports A, N, H, L
//		ClassLoader classLoader = this.getClass().getClassLoader();
//		InputStream stream = classLoader.getResourceAsStream("eds-labs.csv");
//		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
//		String line = reader.readLine();
//		while (line != null) {
//			String[] elements = line.split(",");
//			String l = elements[1];
//			LoincId loincId = new LoincId(l);
//			list.add(loincId);
//			line = reader.readLine();
//		};
		Map<LoincId, Loinc2HpoAnnotation> annotationMap = annotationService.getAnnotationsMap();
		System.out.println("LOINC, Interpretation, Negated, HPO Term Id, HPO Term Description");
		
		for (LoincId loincId : annotationMap.keySet()) {
			try {
				Loinc2HpoAnnotation annotation = annotationService.getAnnotations(loincId);
				if (annotation instanceof DefaultLoinc2HpoAnnotation) {
					DefaultLoinc2HpoAnnotation defaultAnnotation = (DefaultLoinc2HpoAnnotation) annotation;
					List<Map<String, String>> terms = getHpoTerms(defaultAnnotation);
					for (Map<String,String> map : terms) {
						System.out.println(loincId + "," + map.get("code") + "," + map.get("negated") + "," + map.get("termid") + "," + map.get("term"));
					}
				}
			} catch (LoincNotAnnotatedException e) {
				//Move on to the next
			}
		}
		
		//reader.close();
		//stream.close();
		
	}

	private List<Map<String, String>> getHpoTerms(DefaultLoinc2HpoAnnotation annotation) {
		List<Map<String, String>> relatedHpoTerms = new ArrayList<>();
		String json = annotation.toString();
		ObjectMapper om = new ObjectMapper();
		TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
		};
		try {
			HashMap<String, Object> mappingsObj = om.readValue(json, typeRef);
			List<Map<String, Object>> mappings = (List<Map<String, Object>>) mappingsObj.get("mappings");
			for (Map<String, Object> mapping : mappings) {
				Map<String, String> map = new HashMap<>();
				Boolean negated = Boolean.parseBoolean((String) mapping.get("termNegated"));
				TermPrefix prefix = new TermPrefix((String) mapping.get("termPrefix"));
				TermId termId = new TermId(prefix, (String) mapping.get("termId"));
				Term term = hpoService.getTermForTermId(termId);
				map.put("code", (String) mapping.get("code"));
				map.put("negated", negated.toString());
				map.put("termid", termId.getIdWithPrefix());
				map.put("term", (negated ? "EXCLUDED: " : "") + term.getName());
				relatedHpoTerms.add(map);

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return relatedHpoTerms;
	}

}
