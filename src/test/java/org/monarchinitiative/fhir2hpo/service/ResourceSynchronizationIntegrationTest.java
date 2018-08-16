package org.monarchinitiative.fhir2hpo.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;
import org.monarchinitiative.fhir2hpo.loinc.Loinc2HpoAnnotation;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * This ensures that services have been autowired and that all annotations have a corresponding HPO Term in the obo. 
 * These resources sometimes get out of sync.
 * 
 * @author yateam
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HpoService.class, AnnotationService.class}, loader = AnnotationConfigContextLoader.class)
public class ResourceSynchronizationIntegrationTest {

	@Autowired
	HpoService hpoService;
	
	@Autowired
	AnnotationService annotationService;
	
	@Test
	public void testHpoServiceExists() {
		assertNotNull("The HPOService must be injected.", hpoService);
	}
	
	@Test
	public void testAnnotationServiceExists() {
		assertNotNull("The AnnotationService must be injected.", annotationService);
	}
	
	@Test
	public void testHpoTermsExist() {
		List<String> missingTerms = new ArrayList<>();
		Map<LoincId, Loinc2HpoAnnotation> map = annotationService.getAnnotationsMap();
		for (Loinc2HpoAnnotation annotation : map.values()) {
			Collection<HpoTermWithNegation> terms = annotation.getHpoTerms();
			for (HpoTermWithNegation term : terms) {
				Term matchingTerm = hpoService.getTermForTermId(term.getHpoTermId());
				if (matchingTerm == null) {
					missingTerms.add(term.getHpoTermId().getIdWithPrefix());
				}
			}
		}
		
		assertTrue("All annotated terms must be defined in the HPO. Missing terms: " + missingTerms.stream().collect(Collectors.joining(", ")), missingTerms.isEmpty());
	}
}
