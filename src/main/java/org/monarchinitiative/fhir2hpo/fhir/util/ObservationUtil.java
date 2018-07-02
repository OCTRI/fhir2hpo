package org.monarchinitiative.fhir2hpo.fhir.util;

import java.util.HashSet;
import java.util.Set;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationComponentComponent;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.exception.ConflictingLoincCodesException;
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
	public static LoincId getLoincIdOfObservation(Observation observation) throws LoincException {
		return getLoincIdOfCodeableConcept(observation.getCode());
	}
	
	/**
	 * Get the component LoincIds from an observation
	 * @param observation
	 * @return the set of LoincIds in the component section of the observation
	 * @throws LoincException
	 */
	public static Set<LoincId> getComponentLoincIdsOfObservation(Observation observation) throws LoincException {
		Set<LoincId> loincs = new HashSet<>();
		for (ObservationComponentComponent component : observation.getComponent()) {
			try {
			   LoincId loinc = getLoincIdOfCodeableConcept(component.getCode());
			   loincs.add(loinc);
			} catch (ConflictingLoincCodesException e) {
				throw(e);
			} catch (LoincCodeNotFoundException e) {
				// Do nothing if a loinc is not found. Other components might have one.
			}
		}
		return loincs;
	}
	
	// TODO: Only one Loinc per codeable concept is currently allowed. We should expand this, because this is not always 
	// true in practice.
	private static LoincId getLoincIdOfCodeableConcept(CodeableConcept codeableConcept) throws LoincException {
		LoincId loincId = null;
		for (Coding coding : codeableConcept.getCoding()) {
			if (coding.getSystem() != null && coding.getSystem().equals(LOINC_SYSTEM)) {
				if (loincId != null && !loincId.getCode().equals(coding.getCode())) {
					throw new ConflictingLoincCodesException();
				} else {
					loincId = new LoincId(coding.getCode());
				}
			}
		}
		if (loincId == null) {
			throw new LoincCodeNotFoundException();
		}
		return loincId;
	}

}
