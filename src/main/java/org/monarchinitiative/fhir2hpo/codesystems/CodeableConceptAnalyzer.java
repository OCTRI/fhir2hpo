package org.monarchinitiative.fhir2hpo.codesystems;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Coding;
import org.monarchinitiative.fhir2hpo.loinc.exception.ConflictingInternalCodesException;
import org.monarchinitiative.fhir2hpo.loinc.exception.ConversionException;
import org.monarchinitiative.fhir2hpo.loinc.exception.UnmappedCodeableConceptException;

public class CodeableConceptAnalyzer {

	/**
	 * Look for a single internal mapping for the CodeableConcept. Throw an exception if none is found
	 * or if more than one distinct code is encountered.
	 * 
	 * @param codeableConcept
	 * @return the single internal code for the concept
	 * @throws ConversionException
	 */
	public static HpoEncodedValue getInternalCodeForCodeableConcept(CodeableConcept codeableConcept)
			throws ConversionException {

		if (codeableConcept != null) {
			List<Coding> codings = codeableConcept.getCoding();
			Set<HpoEncodedValue> distinctCodes = codings.stream()
					.map(coding -> CodeContainer.getInternalCode(coding)).filter(coding -> coding != null)
					.collect(Collectors.toSet());
			if (distinctCodes.size() == 0) {
				throw new UnmappedCodeableConceptException();
			} else if (distinctCodes.size() > 1) {
				throw new ConflictingInternalCodesException(
						"The CodeableConcept resolves to multiple internal codes: " + distinctCodes.stream()
								.map(HpoEncodedValue::toString).collect(Collectors.joining()));
			} else {
				return distinctCodes.iterator().next();
			}
		}
		return null;
	}

}
