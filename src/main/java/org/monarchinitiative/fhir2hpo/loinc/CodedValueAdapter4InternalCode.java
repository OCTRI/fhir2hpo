package org.monarchinitiative.fhir2hpo.loinc;

import org.monarchinitiative.fhir2hpo.codesystems.Loinc2HpoCodedValue;

public class CodedValueAdapter4InternalCode {

	public static CodedValue toCodedValue(Loinc2HpoCodedValue internal) {
		return new CodedValue(internal.getSystem(), internal.toString());
	}
}
