package org.monarchinitiative.fhir2hpo.loinc;

/**
 * Contains relevant LOINC info - the id, scale, and name
 * 
 * @author yateam
 *
 */
public class Loinc {

	private LoincId loincId;
	private LoincScale loincScale;
	private String displayName;

	public Loinc(LoincId loincId, LoincScale loincScale, String displayName) {
		super();
		this.loincId = loincId;
		this.loincScale = loincScale;
		this.displayName = displayName;
	}

	public LoincId getLoincId() {
		return loincId;
	}

	public void setLoincId(LoincId loincId) {
		this.loincId = loincId;
	}

	public LoincScale getLoincScale() {
		return loincScale;
	}

	public void setLoincScale(LoincScale loincScale) {
		this.loincScale = loincScale;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}
