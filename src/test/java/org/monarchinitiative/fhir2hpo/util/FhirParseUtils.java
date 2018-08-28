package org.monarchinitiative.fhir2hpo.util;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.hl7.fhir.dstu3.model.Observation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

/**
 * Utility for processing FHIR STU3 resources.
 * 
 * @author yateam
 *
 */
public class FhirParseUtils {
	
	// This is expensive and should only be declared once
	private static final FhirContext fhirContext = FhirContext.forDstu3();
	
	public FhirContext getStu3FhirContext() {
		return fhirContext;
	}

	// Parse an observation given the path to the file
	public static Observation getObservation(String path) {
		IParser parser = fhirContext.newJsonParser();
		InputStream stream = FhirParseUtils.class.getClassLoader().getResourceAsStream(path);
		return (Observation) parser.parseResource(new InputStreamReader(stream));
	}

}
