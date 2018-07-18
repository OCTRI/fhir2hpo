# FHIR to HPO

This library converts FHIR STU3 Observations to Human Phenotype Ontology (HPO) Terms when LOINC codes and interpretable values are present.

Using the jar requires local installation of the [monarch-initiative phenol library](https://github.com/monarch-initiative/phenol) that defines the domain around the HPO. 

Once phenol is installed, compile the library and run the tests using `mvn clean install`.

## Using the library

fhir2hpo can be included in your maven project as a dependency:

```
		<dependency>
			<groupId>org.monarchinitiative</groupId>
			<artifactId>fhir2hpo</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
```

Use the ObservationAnalysisService provided by this library to perform conversion. In a Spring Boot application, you can autowire this service and access the associated domain by scanning the fhir2hpo packages.

```
package org.myapp.pkg;

@SpringBootApplication
@EntityScan(basePackages = { "org.myapp.pkg", "org.monarchinitiative.fhir2hpo" })
@ComponentScan({ "org.myapp.pkg", "org.monarchinitiative.fhir2hpo" })
public class MyApp {

	@Autowired
	ObservationAnalysisService observationAnalysisService;

	public static void main(String[] args) {
		SpringApplication.run(MyApp.class, args);
	}
}
```

Then pass STU3 observations to the service to convert:

```
	Observation observation = getObservationFromFhirServer(...);
	List<HpoConversionResult> hpoConversionResult = observationAnalysisService.convert(observation);
```

## Understanding the HpoConversionResult

The conversion of an observation to an HpoTerm can fail for a variety of reasons. Conversion might also succeed using one method but fail using another. The entity HpoConversionResult is used to encapsulate the full context of the run so the consumer has detailed information and can decide how to handle.

The convert method actually passes back a list of conversion results. This is because in rare cases an Observation might contain multiple Loinc codes, and it is possible that these codes are annotated differently in our system. See the below example:

```
{
	"resourceType": "Observation",
	...
	"code": {
		"coding": [{
			"system": "http://loinc.org",
			"code": "55284-4",
			"display": "Blood pressure systolic and diastolic",
			"userSelected": false
		}, {
			"system": "http://loinc.org",
			"code": "8716-3",
			"display": "Vital Signs grouping",
			"userSelected": false
		}],
		"text": "BP"
	}
	...
}
```

Converting this observation would return two results, one for 55284-4 and another for 8716-3. The HpoConversionResult contains getters for the relevant LoincId and original Observation plus methods for interrogating success or failure of the conversion as a whole or specific methods.

The conversion as a whole may fail if Loinc codes are not found in the observation or are not annotated by the library. Assuming conversion is possible, three different methods are attempted. These will proceed regardless of the success or failure of previous methods.

1. Interpretation: Look for an interpretation code in the observation that can be mapped to an HPO Term.
2. Value Quantity and Reference Range: Determine whether the value is low, high, or within range and map to an Hpo Term
3. Value String: Look for a Value String that can be interpreted seeking common terms like "positive", "negative", etc.

It is possible, though rare, that the HpoTerm for one method will not match the term for another. In this case, all terms are returned in the result, and the consumer can decide how to handle.

## Examples

There are two open-source projects using the library that can serve as examples.

This proof-of-concept [web application](https://github.com/OCTRI/poc-hpo-on-fhir) searches for users in the SMART Health IT STU3 sandbox and converts their observations to HPO Terms.

This [statistics gatherer](https://github.com/OCTRI/f2hstats) collects observations from several sandboxes and stores the conversion results in a database where it is easy to qualify the types of observations encountered and the successes/failures of the library. Currently it can only communicate with a small set of unauthenticated sandboxes, but it can be easily adapted to capture real EHR data and even to obscure any PHI so that only aggregate informaton is recorded.

The statistics gatherer also communicates with STU2 servers and provides an example of converting the responses to STU3 so they can be used by the library.