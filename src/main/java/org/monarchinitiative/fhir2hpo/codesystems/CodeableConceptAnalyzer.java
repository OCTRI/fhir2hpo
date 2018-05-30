package org.monarchinitiative.fhir2hpo.codesystems;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.monarchinitiative.fhir2hpo.loinc.exception.ConversionException;
import org.monarchinitiative.fhir2hpo.loinc.exception.ConversionException.ConversionExceptionType;

public class CodeableConceptAnalyzer {

	/**
	 * Look for a single internal mapping for the CodeableConcept. Throw an exception if none is found
	 * or if more than one distinct code is encountered.
	 * 
	 * @param codeableConcept
	 * @return the single internal code for the concept
	 * @throws UnmappedCodeableConceptException
	 * @throws ConflictingInternalCodesException
	 */
	public static Loinc2HpoCodedValue getInternalCodeForCodeableConcept(CodeableConcept codeableConcept)
			throws ConversionException {

		if (codeableConcept != null) {
			List<Coding> codings = codeableConcept.getCoding();
			Set<Loinc2HpoCodedValue> distinctCodes = codings.stream()
					.map(coding -> CodeContainer.getInternalCode(coding)).filter(coding -> coding != null)
					.collect(Collectors.toSet());
			if (distinctCodes.size() == 0) {
				throw new ConversionException(ConversionExceptionType.UNMAPPED_CODEABLE_CONCEPT);
			} else if (distinctCodes.size() > 1) {
				throw new ConversionException(ConversionExceptionType.CONFLICTING_INTERNAL_CODES,
						"The CodeableConcept resolves to multiple internal codes: " + distinctCodes.stream()
								.map(Loinc2HpoCodedValue::toString).collect(Collectors.joining()));
			} else {
				return distinctCodes.iterator().next();
			}
		}
		return null;
	}

}
