package org.monarchinitiative.fhir2hpo.loinc;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties("resourceType")
public class LoincResponseInfo {
	
	private LoincScale loincScale;
	private String displayName;
	
	
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

	// TODO: This parse method is fairly horrifying because the response format is deeply nested and dynamically structured.
	// Is there a cleaner way? https://www.baeldung.com/jackson-nested-values
	@JsonProperty("parameter")
	private void parseParameters(List<Object> parameters) {
		for (Object parameter : parameters) {
			Map<String, Object> map = (Map<String, Object>) parameter;
			if (map.get("name").equals("display")) {
				displayName = (String) map.get("valueString");
			}
			
			if (map.get("name").equals("property")) {
				List<Object> parts = (List<Object>) map.get("part");
				// Part should have one object for code and one for value. Need both to determine if we're reading scale type and what it is
				Boolean scaleType = false;
				String value = null;
				for (Object part : parts) {
					Map<String,Object> partsMap = (Map<String,Object>) part;
					if ("code".equals(partsMap.get("name")) && "SCALE_TYP".equals(partsMap.get("valueCode"))) {
						scaleType = true;
					}
					if ("value".equals(partsMap.get("name")) && partsMap.containsKey("valueCoding")) {
						Map<String,Object> valueCoding = (Map<String,Object>) partsMap.get("valueCoding");
						value = (String) valueCoding.get("display");
					}
				}
				if (scaleType && value != null) {
					loincScale = LoincScale.string2enum(value);
				}
			}
			
		}
	}
}
