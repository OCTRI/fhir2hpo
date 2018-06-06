package org.monarchinitiative.fhir2hpo.codesystems;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.junit.Test;
import org.monarchinitiative.fhir2hpo.loinc.exception.ConversionException;
import org.monarchinitiative.fhir2hpo.loinc.exception.ConversionException.ConversionExceptionType;
import org.monarchinitiative.fhir2hpo.util.FhirMockUtils;

public class CodeableConceptAnalyzerTest {
	
	@Test
	public void testOneCode() throws ConversionException {
		List<Coding> codings = new ArrayList<>();
		codings.add(FhirMockUtils.mockCoding("http://hl7.org/fhir/v2/0078", "LL"));
		CodeableConcept codeableConcept = FhirMockUtils.mockCodeableConcept(codings);
		Loinc2HpoCodedValue internalCode = CodeableConceptAnalyzer.getInternalCodeForCodeableConcept(codeableConcept);
		assertEquals("Expected internal code 'L' for external code 'LL'", "L", internalCode.name());
	}

	@Test
	public void testOneDistinctCode() throws ConversionException {
		List<Coding> codings = new ArrayList<>();
		codings.add(FhirMockUtils.mockCoding("http://hl7.org/fhir/v2/0078", "LL"));
		codings.add(FhirMockUtils.mockCoding("http://hl7.org/fhir/v2/0078", "L"));
		CodeableConcept codeableConcept = FhirMockUtils.mockCodeableConcept(codings);
		Loinc2HpoCodedValue internalCode = CodeableConceptAnalyzer.getInternalCodeForCodeableConcept(codeableConcept);
		assertEquals("Expected internal code 'L' for external codes 'LL' and 'L'", "L", internalCode.name());
	}

	public void testUnmappedSystem() {
		List<Coding> codings = new ArrayList<>();
		codings.add(FhirMockUtils.mockCoding("Other System", "LL"));
		CodeableConcept codeableConcept = FhirMockUtils.mockCodeableConcept(codings);
		try {
			CodeableConceptAnalyzer.getInternalCodeForCodeableConcept(codeableConcept);
		} catch (ConversionException e) {
			assertEquals("Expected the system to be unmapped", ConversionExceptionType.UNMAPPED_CODEABLE_CONCEPT, e.getType());
		}
	}

	public void testUnmappedCode() {
		List<Coding> codings = new ArrayList<>();
		codings.add(FhirMockUtils.mockCoding("http://hl7.org/fhir/v2/0078", "Other Code"));
		CodeableConcept codeableConcept = FhirMockUtils.mockCodeableConcept(codings);
		try {
			CodeableConceptAnalyzer.getInternalCodeForCodeableConcept(codeableConcept);
		} catch (ConversionException e) {
			assertEquals("Expected the code to be unmapped", ConversionExceptionType.UNMAPPED_CODEABLE_CONCEPT, e.getType());
		}
	}

	public void testConflictingCodes() {
		List<Coding> codings = new ArrayList<>();
		codings.add(FhirMockUtils.mockCoding("http://hl7.org/fhir/v2/0078", "LL"));
		codings.add(FhirMockUtils.mockCoding("http://hl7.org/fhir/v2/0078", "HH"));
		CodeableConcept codeableConcept = FhirMockUtils.mockCodeableConcept(codings);
		try {
			CodeableConceptAnalyzer.getInternalCodeForCodeableConcept(codeableConcept);
		} catch (ConversionException e) {
			assertEquals("Expected conflicting internal codes", ConversionExceptionType.CONFLICTING_INTERNAL_CODES, e.getType());
		}
	}

}
