package org.monarchinitiative.fhir2hpo.codesystems;

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

}
