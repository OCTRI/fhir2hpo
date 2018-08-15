package org.monarchinitiative.fhir2hpo.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.monarchinitiative.fhir2hpo.io.LoincAnnotationParser;
import org.monarchinitiative.fhir2hpo.io.NonInterpretableLoincParser;
import org.monarchinitiative.fhir2hpo.loinc.Loinc2HpoAnnotation;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincNotAnnotatedException;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.io.obo.hpo.HpOboParser;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;

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

		// Load the HPO
		URL url = classLoader.getResource("hp.obo");
		Path p = Paths.get(url.toURI());
		HpOboParser hpoOboParser = new HpOboParser(p.toFile());
		HpoOntology ontology = hpoOboParser.parse();

		ImmutableMap.Builder<TermId, Term> termmapBuilder = new ImmutableMap.Builder<>();
		// for some reason there is a bug here...issue #34 on ontolib tracker
		// here is a workaround to remove duplicate entries
		List<Term> res = ontology.getTermMap().values().stream().distinct()
				.collect(Collectors.toList());

		res.forEach(term -> termmapBuilder.put(term.getId(), term));
		ImmutableMap<TermId, Term> termmap = termmapBuilder.build();

		// Now load the standard annotations.
		loincMap.putAll(LoincAnnotationParser.parse(classLoader.getResourceAsStream("annotations.tsv"), termmap));
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
