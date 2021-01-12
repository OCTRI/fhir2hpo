# FHIR to HPO

This library converts FHIR Observations to Human Phenotype Ontology (HPO) Terms when LOINCs and interpretable values are present. It accepts R5 FHIR Resources formatted using the [HAPI-FHIR Java library](https://hapifhir.io/). HAPI-FHIR also provides a number of [converters](https://hapifhir.io/hapi-fhir/docs/model/converter.html) that can be used to communicate with previous FHIR versions, so you are not limited to using this library on an R5 server.

# Setup

Compiling this library requires installation of [Maven](http://maven.apache.org/install.html) and [Java 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

In addition, you need to locally install the [monarch-initiative phenol library](https://github.com/monarch-initiative/phenol) that defines the domain around the HPO. This project is not yet available on Maven Central. From the command line:

```
git clone https://github.com/monarch-initiative/phenol.git
cd phenol
git checkout v.1.2.6
mvn install
``` 

This checks out and builds Release 1.2.6 of phenol. 

Once phenol is installed, install the fhir2hpo library. You can either clone this repo and build from master or check out a release tag. Run `mvn clean install` to build and deposit the new dependency into your maven repository.

## Using the library

fhir2hpo can be included in your maven project as a dependency:

```
<dependency>
	<groupId>org.monarchinitiative</groupId>
	<artifactId>fhir2hpo</artifactId>
	<version>1.0.5</version>
</dependency>
```

To perform conversion of a FHIR Observation to HPO Terms, use the ObservationAnalysisService provided by this library. In a Spring Boot application, you can autowire this service and access the associated domain by scanning the fhir2hpo packages.

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

Then pass observations to the service to convert:

```
Observation observation = getObservationFromFhirServer(...);
ObservationConversionResult observationConversionResult = observationAnalysisService.convert(observation);
```

Two other services are provided that may be useful. The HpoService provides additional information about an HPO Term given a TermId. This is where you would find the term name (e.g., HP:0003573 => Increased total bilirubin).

The AnnotationService provides information about how LOINC Codes are mapped to HPO Terms.

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
2. Value Quantity and Reference Range: Look for a reference range provided by the observation, and determine whether the value is low, high, or within range and map to an Hpo Term
3. Value String: Look for a Value String that can be interpreted seeking common terms like "positive", "negative", etc.

It is possible, though rare, that the HpoTerm for one method will not match the term for another. In this case, all terms are returned in the result, and the consumer can decide how to handle.

## Examples

There are two open-source projects using the library that can serve as examples.

This proof-of-concept [web application](https://github.com/OCTRI/poc-hpo-on-fhir) searches for users in a FHIR sandbox and converts their observations to HPO Terms. This application is capable of communicating with earlier versions of FHIR servers and provides examples of converting the responses to R5 so they can be used by the library.

This [statistics gatherer](https://github.com/OCTRI/f2hstats) collects observations from several sandboxes and stores the conversion results in a database where it is easy to qualify the types of observations encountered and the successes/failures of the library. Currently it can only communicate with a small set of unauthenticated sandboxes, but it can be easily adapted to capture real EHR data and even to obscure any PHI so that only aggregate information is recorded. This should work with release 1.0.3 of this library which expected STU3 observations.
