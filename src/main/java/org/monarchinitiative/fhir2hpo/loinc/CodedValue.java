package org.monarchinitiative.fhir2hpo.loinc;

import org.hl7.fhir.dstu3.model.Coding;
import org.monarchinitiative.fhir2hpo.codesystems.Loinc2HpoCodedValue;

/**
 * This class is very similar to org.hl7.fhir.dstu3.model.Coding. We override the equals() method so that any two codings with the same system and code are considered identical.
 */

public class CodedValue {

	private Coding coding;

	public CodedValue(String system, String code) {
		this.coding = new Coding();
		this.coding.setSystem(system).setCode(code);
	}

	public CodedValue(Loinc2HpoCodedValue loinc2HpoCodedValue) {
		this.coding = new Coding();
		this.coding.setSystem(loinc2HpoCodedValue.getSystem()).setCode(loinc2HpoCodedValue.toString());
	}

	public Coding getCoding() {
		return this.coding;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return this.coding.getSystem().hashCode() + prime * this.coding.getCode().hashCode();

	}

	@Override
	public boolean equals(Object o) {
		if (o == this.coding) {
			return true;
		}

		if (! (o instanceof Coding)) {
			return false;
		}

		Coding other = (Coding) o;

		return other.getSystem().equals(this.coding.getSystem())
			&& other.getCode().equals(this.coding.getCode());
	}

}
