package org.monarchinitiative.fhir2hpo.fhir.util;

import java.util.HashSet;
import java.util.Set;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationComponentComponent;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincCodeNotFoundException;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincException;

public class ObservationUtil {

	private static final String LOINC_SYSTEM = "http://loinc.org";

	/**
	 * Get the LoincId from a FHIR observation.
	 * 
	 * @return the single LoincId in the code section of the observation 
	 * @throws LoincException
	 */
	public static Set<LoincId> getLoincIdsOfObservation(Observation observation) throws LoincException {
		return getLoincIdsOfCodeableConcept(observation.getCode());
	}
	
	/**
	 * Get the component LoincIds from an observation
	 * @param observation
	 * @return the set of LoincIds in the component section of the observation
	 * @throws LoincException
	 */
	public static Set<LoincId> getComponentLoincIdsOfObservation(Observation observation) throws LoincException {
		// TODO: Make this more useable. We should return which components are associated with which loincs.
		// Note that multiple loincs may be associated with a single component.
		Set<LoincId> loincs = new HashSet<>();
		for (ObservationComponentComponent component : observation.getComponent()) {
			try {
			   Set<LoincId> componentLoincs = getLoincIdsOfCodeableConcept(component.getCode());
			   loincs.addAll(componentLoincs);
			} catch (LoincCodeNotFoundException e) {
				// Do nothing if a loinc is not found. Other components might have one.
			}
		}
		return loincs;
	}
	
	/**
	 * For a codeable concept, get any LOINC Ids associated
	 * @param codeableConcept
	 * @return
	 * @throws LoincException
	 */
	private static Set<LoincId> getLoincIdsOfCodeableConcept(CodeableConcept codeableConcept) throws LoincException {
		Set<LoincId> loincIds = new HashSet<>();
		for (Coding coding : codeableConcept.getCoding()) {
			if (coding.getSystem() != null && coding.getSystem().equals(LOINC_SYSTEM)) {
				loincIds.add(new LoincId(coding.getCode()));
			}
		}
		if (loincIds.isEmpty()) {
			throw new LoincCodeNotFoundException();
		}
		return loincIds;
	}

}
