package org.monarchinitiative.fhir2hpo.codesystems;

import org.hl7.fhir.dstu3.model.Coding;

/**
 * This class is very similar to org.hl7.fhir.dstu3.model.Coding. We override the equals() method so that any two 
 * codings with the same system and code are considered identical. Some default encodings are defined for use in most 
 * internal mappings.
 */
public class HpoEncodedValue {
	
	public static final String FHIR_SYSTEM = "FHIR";
	public static final HpoEncodedValue ABNORMAL = new HpoEncodedValue(FHIR_SYSTEM, "A");
	public static final HpoEncodedValue LOW = new HpoEncodedValue(FHIR_SYSTEM, "L");
	public static final HpoEncodedValue NORMAL = new HpoEncodedValue(FHIR_SYSTEM, "N");
	public static final HpoEncodedValue HIGH = new HpoEncodedValue(FHIR_SYSTEM, "H");
	public static final HpoEncodedValue UNKNOWN = new HpoEncodedValue(FHIR_SYSTEM, "U");

	private Coding coding;

	public HpoEncodedValue(String system, String code) {
		this.coding = new Coding();
		
		if (system.equals(FHIR_SYSTEM)) {
			if (code.equals("POS")) {
				code = "H";
			} else if (code.equals("NEG")) {
				code = "N";
			}
		}
		
		this.coding.setSystem(system).setCode(code);
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
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		HpoEncodedValue other = (HpoEncodedValue) obj;
		return other.getCoding().getSystem().equals(this.coding.getSystem()) && 
				other.getCoding().getCode().equals(this.coding.getCode());
	}

	@Override
	public String toString() {
		return "system: " + this.coding.getSystem() + " code: " + this.coding.getCode();
	}

}
