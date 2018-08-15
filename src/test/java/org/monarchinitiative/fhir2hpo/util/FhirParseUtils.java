package org.monarchinitiative.fhir2hpo.util;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.hl7.fhir.dstu3.model.Observation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

public class FhirParseUtils {

	// TODO: Passing the context here doesn't make much sense since we are returning a STU3 observation always. Fix!
	// Parse an observation given the path to the file
	public static Observation getObservation(FhirContext fhirContext, String path) {
		IParser parser = fhirContext.newJsonParser();
		InputStream stream = FhirParseUtils.class.getClassLoader().getResourceAsStream(path);
		return (Observation) parser.parseResource(new InputStreamReader(stream));
	}

}
