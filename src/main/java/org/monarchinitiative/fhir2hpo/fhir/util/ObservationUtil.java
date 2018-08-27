package org.monarchinitiative.fhir2hpo.fhir.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationComponentComponent;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincException;

public class ObservationUtil {

	private static final String LOINC_SYSTEM = "http://loinc.org";

	public static Set<LoincId> getAllLoincIdsOfObservation(Observation observation) {
		Set<LoincId> loincIds = new HashSet<>();
		loincIds.addAll(getCodeSectionLoincIdsOfObservation(observation));
		loincIds.addAll(getComponentLoincIdsOfObservation(observation).keySet());
		return loincIds;
	}
	
	/**
	 * Get the code section LoincId(s) from a FHIR observation.
	 * 
	 * @return the LoincId(s) in the code section of the observation 
	 */
	public static Set<LoincId> getCodeSectionLoincIdsOfObservation(Observation observation) {
		return getLoincIdsOfCodeableConcept(observation.getCode());
	}
	
	/**
	 * Get the component LoincIds from an observation and their corresponding component. Note that a component may
	 * have multiple LOINCs. This would result in multiple map entries for the same component.
	 * @param observation
	 * @return the component LoincIds and their corresponding component
	 */
	public static Map<LoincId, ObservationComponentComponent> getComponentLoincIdsOfObservation(Observation observation) {
		Map<LoincId, ObservationComponentComponent> loincs = new HashMap<>();
		for (ObservationComponentComponent component : observation.getComponent()) {
			Set<LoincId> componentLoincs = getLoincIdsOfCodeableConcept(component.getCode());
			componentLoincs.stream().forEach(loinc -> loincs.put(loinc, component));
		}
		return loincs;
	}	
	
	/**
	 * For a codeable concept, get any LOINC Ids associated
	 * @param codeableConcept
	 * @return
	 * @throws LoincException 
	 */
	public static Set<LoincId> getLoincIdsOfCodeableConcept(CodeableConcept codeableConcept) {
		Set<LoincId> loincIds = new HashSet<>();
		for (Coding coding : codeableConcept.getCoding()) {
			if (coding.getSystem() != null && coding.getSystem().equals(LOINC_SYSTEM)) {
				try {
					loincIds.add(new LoincId(coding.getCode()));
				} catch (LoincException e) {
					// LoincFormatException will be ignored. This is just bad data and won't be processed.
				}
			}
		}
		return loincIds;
	}
	
	/**
	 * Given a CodeableConcept, try to extract a description. Text is preferred, but if not
	 * found, look through codings.
	 * @param codeableConcept
	 * @return return a description of the CodeableConcept or null if not found.
	 */
	public static String getDescriptionOfCodeableConcept(CodeableConcept codeableConcept) {
		
		if (codeableConcept.hasText()) {
			return codeableConcept.getText();
		}
		if (codeableConcept.hasCoding()) {			
			// Get the first coding with a display
			Optional<Coding> codingWithDisplay = codeableConcept.getCoding().stream().filter(coding -> coding.getDisplay() != null).findFirst();
			if (codingWithDisplay.isPresent()) {
				return codingWithDisplay.get().getDisplay();
			}			
		}
		
		return null;
		
	}

}
