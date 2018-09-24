package org.monarchinitiative.fhir2hpo.service;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.io.obo.hpo.HpOboParser;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;

/**
 * This service constructs a TermMap from the HPO resource.
 * 
 * @author yateam
 *
 */
@Service
public class HpoService {
	
	HpoOntology ontology;

	ImmutableMap<TermId, Term> termmap;

	public HpoService() throws URISyntaxException, PhenolException {
		// Load the HPO
		InputStream obo = getClass().getClassLoader().getResourceAsStream("hp.obo");
		HpOboParser hpoOboParser = new HpOboParser(obo);
		this.ontology = hpoOboParser.parse();

		ImmutableMap.Builder<TermId, Term> termmapBuilder = new ImmutableMap.Builder<>();
		// Terms should be unique: https://github.com/Phenomics/ontolib/issues/34
		// Here is a workaround to remove duplicate entries
		List<Term> res = ontology.getTermMap().values().stream().distinct()
			.collect(Collectors.toList());

		res.forEach(term -> termmapBuilder.put(term.getId(), term));
		termmap = termmapBuilder.build();
	}

	public Term getTermForTermId(TermId termId) {
		return termmap.get(termId);
	}
	
	public Set<HpoTermWithNegation> getChildren(HpoTermWithNegation termWithNegation) {
		Set<TermId> children = ontology.subOntology(termWithNegation.getHpoTermId()).getAllTermIds();
		Boolean isNegated = termWithNegation.isNegated();
		return children.stream().map(termId -> new HpoTermWithNegation(termId, isNegated)).collect(Collectors.toSet());
	}

}
