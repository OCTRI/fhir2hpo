package org.monarchinitiative.fhir2hpo.codesystems;

import org.monarchinitiative.fhir2hpo.loinc.CodedValue;

/**
 * Defines the 5 internal codes that all external system codes must be able to map to.
 * 
 * @author yateam
 *
 */
public enum Loinc2HpoCodedValue {
	A("Abnormal"), 
	L("Low"), 
	N("Normal"), 
	H("High"), 
	U("Unknown");

	private String display;
	private final String system = "FHIR";

	Loinc2HpoCodedValue(String display) {
		this.display = display;
	}

	public String getDisplay() {
		return display;
	}

	public String getSystem() {
		return system;
	}

	/**
	 * Use this method instead of the built-in valueOf enum method. It maps additional strings provided by the
	 * annotations file.
	 * 
	 * @param name
	 * @return the coded value
	 */
	public static Loinc2HpoCodedValue getCodedValue(String name) {

		if (name.equals("POS")) {
			return Loinc2HpoCodedValue.H;
		} else if (name.equals("NEG")) {
			return Loinc2HpoCodedValue.N;
		}

		return valueOf(name);

	}

}
