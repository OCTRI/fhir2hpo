package org.monarchinitiative.fhir2hpo.util;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.hl7.fhir.dstu3.model.Observation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

public class FhirParseUtils {

	// Parse an observation given the path to the file
	public static Observation getObservation(FhirContext fhirContext, String path) {
		IParser parser = fhirContext.newJsonParser();
		InputStream stream = FhirParseUtils.class.getClassLoader().getResourceAsStream(path);
		return (Observation) parser.parseResource(new InputStreamReader(stream));
	}

}
