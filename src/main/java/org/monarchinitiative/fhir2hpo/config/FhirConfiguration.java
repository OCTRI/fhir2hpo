package org.monarchinitiative.fhir2hpo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.uhn.fhir.context.FhirContext;

/**
 * Configuration for interacting with FHIR messages.
 * 
 * TODO: Support other FHIR versions
 * @author yateam
 *
 */
@Configuration
public class FhirConfiguration {
	
	@Bean
	public FhirContext getFhirContext() {
		return FhirContext.forDstu3();
	}

}
