package org.monarchinitiative.fhir2hpo.fhir.util;

import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Observation;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincException;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincException.LoincExceptionType;

public class ObservationUtil {
	
	private static final String LOINC_SYSTEM = "http://loinc.org";

	/**
	 * A method to get the LoincId from a FHIR observation
	 * 
	 * @return
	 * @throws LoincException
	 */
	public static LoincId getLoincIdOfObservation(Observation observation) throws LoincException {
		LoincId loincId = null;
		for (Coding coding : observation.getCode().getCoding()) {
			if (coding.getSystem().equals(LOINC_SYSTEM)) {
				if (loincId != null && !loincId.getCode().equals(coding.getCode())) {
					throw new LoincException(LoincExceptionType.CONFLICTING_LOINC_CODES);
				} else {
					loincId = new LoincId(coding.getCode());
				}
			}
		}
		if (loincId == null)
			throw new LoincException(LoincExceptionType.LOINC_CODE_NOT_FOUND);
		return loincId;
	}

}
