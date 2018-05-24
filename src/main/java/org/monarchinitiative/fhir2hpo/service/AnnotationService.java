package org.monarchinitiative.fhir2hpo.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.monarchinitiative.fhir2hpo.io.LoincAnnotationParser;
import org.monarchinitiative.fhir2hpo.loinc.Loinc2HpoAnnotation;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.io.obo.hpo.HpoOboParser;
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
	
	public AnnotationService() throws IOException {

		ClassLoader classLoader = getClass().getClassLoader();
		File hpo = new File(classLoader.getResource("hp.obo").getFile());
		HpoOboParser hpoOboParser = new HpoOboParser(hpo);
		HpoOntology ontology = hpoOboParser.parse();
		
        ImmutableMap.Builder<TermId,Term> termmapBuilder = new ImmutableMap.Builder<>();
        // for some reason there is a bug here...issue #34 on ontolib tracker
        // here is a workaround to remove duplicate entries
        List<Term> res = ontology.getTermMap().values().stream().distinct()
                .collect(Collectors.toList());

        res.forEach( term -> termmapBuilder.put(term.getId(), term));
        ImmutableMap<TermId, Term> termmap = termmapBuilder.build();

		File annotations = new File(classLoader.getResource("annotations.tsv").getFile());
        this.loincMap = LoincAnnotationParser.parse(annotations, termmap);
	}
	
	public Map<LoincId, Loinc2HpoAnnotation> getAnnotationsMap() {
		return loincMap;
	}

	public Loinc2HpoAnnotation getAnnotations(LoincId loincId) {
		return loincMap.get(loincId);
	}

}
