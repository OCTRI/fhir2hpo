# FHIR to HPO

This library converts FHIR STU3 Observations to Human Phenotype Ontology (HPO) Terms when LOINCs and interpretable values are present.

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
ObservationConversionResult observationConversionResult = observationAnalysisService.convert(observation);
```

## Understanding the ObservationConversionResult

In most cases, an observation will contain a single LOINC along with a corresponding value. However, multiple LOINCs are possible, and for LOINC panels, the observation may contain several component LOINCs with individual values corresponding to each. The ObservationConversionResult encapsulates this by providing a list of LoincConversionResults, one for each LOINC in the observation. If the list is empty, then no LOINCs were found.

The conversion of a specific LOINC to an HpoTerm can fail for a variety of reasons. Conversion might also succeed using one method but fail using another. The entity LoincConversionResult encapsulates the full context of the attempted conversion so the consumer has detailed information and can decide how to handle the result. This includes information about success and failure along with a LoincObservationInfo object that breaks out information from the observation specific to the LOINC.

Consider the example below:

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
		"text": "BP",
		"component": [{
				"code": {
					"coding": [{
						"system": "http://loinc.org",
						"code": "8480-6",
						"display": "Systolic blood pressure"
					}]
				},
				"valueQuantity": {
					"value": 107,
					"unit": "mmHg",
					"system": "http://unitsofmeasure.org",
					"code": "mm[Hg]"
				},
				"interpretation": {
					"coding": [{
						"system": "http://hl7.org/fhir/v2/0078",
						"code": "N",
						"display": "normal"
					}],
					"text": "Normal"
				}
			},
			{
				"code": {
					"coding": [{
						"system": "http://loinc.org",
						"code": "8462-4",
						"display": "Diastolic blood pressure"
					}]
				},
				"valueQuantity": {
					"value": 60,
					"unit": "mmHg",
					"system": "http://unitsofmeasure.org",
					"code": "mm[Hg]"
				},
				"interpretation": {
					"coding": [{
						"system": "http://hl7.org/fhir/v2/0078",
						"code": "L",
						"display": "low"
					}],
					"text": "Below low normal"
				}
			}
		]
	}
}
```

Converting this observation would return four LoincConversionResults, 2 from the code section and 2 from the components section. Each result contains getters for the relevant LoincId and LoincObservationInfo. The LoincObservationInfo for "8462-4" would contain the description of the test "Diastolic Blood Pressure" and the accompanying value and interpretation of "60 mmHg" and "Low". The LoincConversionResult also has methods for interrogating success or failure of the conversion as a whole or using specific methods (e.g., Interpretation).

The conversion as a whole may fail if LOINCs are not annotated by the library. Assuming conversion is possible, three different methods are attempted. These will proceed regardless of the success or failure of previous methods.

1. Interpretation: Look for an interpretation code for the LOINC that can be mapped to an HPO Term.
2. Value Quantity and Reference Range: Determine whether the value is low, high, or within range and map to an Hpo Term
3. Value String: Look for a Value String that can be interpreted seeking common terms like "positive", "negative", etc.

It is possible, though rare, that the HpoTerm for one method will not match the term for another. In this case, all terms are returned in the result, and the consumer can decide how to handle.

## Examples

There are two open-source projects using the library that can serve as examples.

This proof-of-concept [web application](https://github.com/OCTRI/poc-hpo-on-fhir) searches for users in the SMART Health IT STU3 sandbox and converts their observations to HPO Terms.

This [statistics gatherer](https://github.com/OCTRI/f2hstats) collects observations from several sandboxes and stores the conversion results in a database where it is easy to qualify the types of observations encountered and the successes/failures of the library. Currently it can only communicate with a small set of unauthenticated sandboxes, but it can be easily adapted to capture real EHR data and even to obscure any PHI so that only aggregate information is recorded.

Both applications are capable of communicating with STU2 servers and provide an example of converting the responses to STU3 so they can be used by the library.