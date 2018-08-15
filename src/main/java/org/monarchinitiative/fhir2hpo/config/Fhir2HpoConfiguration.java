package org.monarchinitiative.fhir2hpo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * This makes the properties file configuration available to tests and to applications that use this
 * library as a dependency.
 * 
 * @author yateam
 *
 */
@Configuration
@PropertySource("classpath:fhir2hpo.properties")
public class Fhir2HpoConfiguration {

}
