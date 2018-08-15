package org.monarchinitiative.fhir2hpo.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
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
	
	public AnnotationService() throws IOException, PhenolException {

		ClassLoader classLoader = getClass().getClassLoader();

		// First load the noninterpretable LOINCs
        loincMap = NonInterpretableLoincParser.parse(classLoader.getResourceAsStream("noninterpretable-annotations.tsv"));

		// TODO: Find a better option for these resources. They have to be retrieved as a stream once they are packaged
		// into a jar, but the HpoOboParser requires a file
		InputStream stream = classLoader.getResourceAsStream("hp.obo");
		File hpo = new File("hp.tmp");
		java.nio.file.Files.copy(stream,
				hpo.toPath(), 
				StandardCopyOption.REPLACE_EXISTING);
		stream.close();
		
		HpOboParser hpoOboParser = new HpOboParser(hpo);
		HpoOntology ontology = hpoOboParser.parse();
		
        ImmutableMap.Builder<TermId,Term> termmapBuilder = new ImmutableMap.Builder<>();
        // for some reason there is a bug here...issue #34 on ontolib tracker
        // here is a workaround to remove duplicate entries
        List<Term> res = ontology.getTermMap().values().stream().distinct()
                .collect(Collectors.toList());

        res.forEach( term -> termmapBuilder.put(term.getId(), term));
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
