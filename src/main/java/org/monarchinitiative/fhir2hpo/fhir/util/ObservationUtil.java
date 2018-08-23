package org.monarchinitiative.fhir2hpo.fhir.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationComponentComponent;
import org.hl7.fhir.dstu3.model.Quantity;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.exceptions.FHIRException;
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
	 * This helper will return a null ValueQuantity instead of throwing an exception when one doesn't exist
	 * @param observation
	 * @return
	 */
	public static Quantity getValueQuantityOfObservation(Observation observation) {
		Quantity valueQuantity = null;
		try {
			valueQuantity = observation.getValueQuantity();
		} catch (FHIRException e) {
			// Leave valueQuantity null
		}
		
		return valueQuantity;
	}

	/**
	 * This helper will return a null ValueQuantity instead of throwing an exception when one doesn't exist
	 * @param component
	 * @return
	 */
	public static Quantity getValueQuantityOfObservationComponent(ObservationComponentComponent component) {
		Quantity valueQuantity = null;
		try {
			valueQuantity = component.getValueQuantity();
		} catch (FHIRException e) {
			// Leave valueQuantity null
		}
		
		return valueQuantity;
	}

	/**
	 * This helper will return a null String instead of throwing an exception when a value is not a ValueString
	 * @param observation
	 * @return
	 */
	public static String getValueStringOfObservation(Observation observation) {
		String valueString = null;
		try {
			StringType valueStringType = observation.getValueStringType();
			if (valueStringType != null) {
				valueString = valueStringType.asStringValue();
			}
		} catch (FHIRException e) {
			// Leave valueString null
		}
		
		return valueString;
	}

	/**
	 * This helper will return a null String instead of throwing an exception when a value is not a ValueString
	 * @param component
	 * @return
	 */
	public static String getValueStringOfObservationComponent(ObservationComponentComponent component) {
		String valueString = null;
		try {
			StringType valueStringType = component.getValueStringType();
			if (valueStringType != null) {
				valueString = valueStringType.asStringValue();
			}
		} catch (FHIRException e) {
			// Leave valueString null
		}
		
		return valueString;
	}

	/**
	 * For a codeable concept, get any LOINC Ids associated
	 * @param codeableConcept
	 * @return
	 * @throws LoincException 
	 */
	private static Set<LoincId> getLoincIdsOfCodeableConcept(CodeableConcept codeableConcept) {
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

}
