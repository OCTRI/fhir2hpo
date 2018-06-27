package org.monarchinitiative.fhir2hpo.loinc;

import org.monarchinitiative.fhir2hpo.codesystems.Loinc2HpoCodedValue;

public class CodedValueAdapter4InternalCode {


//	public CodedValueAdapter4InternalCode(String system, String code) {
//		super(system, code);
//	}

	public static CodedValue toCodedValue(Loinc2HpoCodedValue internal) {
		return new CodedValue(internal.getSystem(), internal.getDisplay());
	}
}
