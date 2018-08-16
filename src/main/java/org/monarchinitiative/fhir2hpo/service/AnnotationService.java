package org.monarchinitiative.fhir2hpo.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.monarchinitiative.fhir2hpo.io.LoincAnnotationParser;
import org.monarchinitiative.fhir2hpo.io.NonInterpretableLoincParser;
import org.monarchinitiative.fhir2hpo.loinc.Loinc2HpoAnnotation;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincNotAnnotatedException;
import org.monarchinitiative.phenol.base.PhenolException;
import org.springframework.stereotype.Service;

/**
 * This service keeps track of the annotations for all LOINC Codes.
 * 
 * @author yateam
 *
 */
@Service
public class AnnotationService {

    Map<LoincId, Loinc2HpoAnnotation> loincMap;

	public AnnotationService() throws IOException, PhenolException, URISyntaxException {

		ClassLoader classLoader = getClass().getClassLoader();

		// First load the noninterpretable LOINCs
		loincMap = NonInterpretableLoincParser
				.parse(classLoader.getResourceAsStream("noninterpretable-annotations.tsv"));

		// Now load the standard annotations.
		loincMap.putAll(LoincAnnotationParser.parse(classLoader.getResourceAsStream("annotations.tsv")));
	}

	public Map<LoincId, Loinc2HpoAnnotation> getAnnotationsMap() {
		return loincMap;
	}

	public Loinc2HpoAnnotation getAnnotations(LoincId loincId) throws LoincNotAnnotatedException {
		if (loincMap.containsKey(loincId)) {
			return loincMap.get(loincId);
		} else {
			throw new LoincNotAnnotatedException("The LOINC Code " + loincId + " has not been annotated.");
		}
	}

}
