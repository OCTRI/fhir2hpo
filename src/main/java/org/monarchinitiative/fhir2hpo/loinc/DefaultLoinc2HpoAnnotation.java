package org.monarchinitiative.fhir2hpo.loinc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationReferenceRangeComponent;
import org.hl7.fhir.dstu3.model.Quantity;
import org.hl7.fhir.exceptions.FHIRException;
import org.monarchinitiative.fhir2hpo.codesystems.CodeableConceptAnalyzer;
import org.monarchinitiative.fhir2hpo.codesystems.Loinc2HpoCodedValue;
import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;
import org.monarchinitiative.fhir2hpo.loinc.exception.ConversionException;
import org.monarchinitiative.fhir2hpo.loinc.exception.ConversionException.ConversionExceptionType;

/**
 * This represents the default annotation implementation where a single observation is parsed
 * to determine the HPO mapping. It will try the following approaches in order:
 * - Find HPO through interpretation CodeableConcept
 * - Find HPO through value quantity
 * - Find HPO through value CodeableConcept
 * 
 * @author yateam
 *
 */
public class DefaultLoinc2HpoAnnotation implements Loinc2HpoAnnotation {

	private LoincId loincId;
	private LoincScale loincScale;
	// Map from internal code to term including negation
	private Map<Loinc2HpoCodedValue, HpoTermWithNegation> codeToHpoTerm;

	public static class Builder {

		private LoincId loincId = null;
		private LoincScale loincScale = null;
		private Map<Loinc2HpoCodedValue, HpoTermWithNegation> codeToHpoTerm = new HashMap<>();

		/**
		 * Set the LOINC Id
		 * 
		 * @param loincId
		 */
		public Builder setLoincId(LoincId loincId) {
			this.loincId = loincId;
			return this;
		}

		/**
		 * Set the LOINC scale
		 * 
		 * @param loincScale
		 */
		public Builder setLoincScale(LoincScale loincScale) {
			this.loincScale = loincScale;
			return this;
		}

		/**
		 * Add an annotation in the advanced mode.
		 * 
		 * @param code
		 * @param annotation
		 * @return
		 */
		public Builder addMapping(Loinc2HpoCodedValue internalCode, HpoTermWithNegation term) {
			this.codeToHpoTerm.put(internalCode, term);
			return this;
		}

		public DefaultLoinc2HpoAnnotation build() {
			return new DefaultLoinc2HpoAnnotation(loincId,
					loincScale,
					codeToHpoTerm);
		}
	}

	private DefaultLoinc2HpoAnnotation(LoincId loincId, LoincScale loincScale,
			Map<Loinc2HpoCodedValue, HpoTermWithNegation> codeToHpoTerm) {
		this.loincId = loincId;
		this.loincScale = loincScale;
		this.codeToHpoTerm = codeToHpoTerm;

		// If a "normal" term is mapped but not an "abnormal" term, create one.
		if (codeToHpoTerm.containsKey(Loinc2HpoCodedValue.N) && !codeToHpoTerm.containsKey(Loinc2HpoCodedValue.A)) {
			HpoTermWithNegation normalTerm = codeToHpoTerm.get(Loinc2HpoCodedValue.N);
			codeToHpoTerm.put(Loinc2HpoCodedValue.A,
					new HpoTermWithNegation(normalTerm.getHpoTerm(), !normalTerm.isNegated()));
		}
	}

	/**
	 * 
	 * @return the LOINC Id
	 */
	public LoincId getLoincId() {
		return loincId;
	}

	/**
	 * 
	 * @return the LOINC scale
	 */
	public LoincScale getLoincScale() {
		return loincScale;
	}

	// TODO: This takes for granted that the observation passed in actually has the loincid associated with this
	// instance.
	// Seems like we should either assert this or reconsider design.
	@Override
	public HpoTermWithNegation convert(Observation observation)
			throws ConversionException, FHIRException {

		if (observation.hasInterpretation()) {
			Loinc2HpoCodedValue internalCode = CodeableConceptAnalyzer
					.getInternalCodeForCodeableConcept(observation.getInterpretation());
			return getHpoTermForInternalCode(internalCode);
		}

		if (observation.hasValueQuantity()) {
			return getHpoTermForValueQuantity(observation.getValueQuantity(), observation.getReferenceRange());
		}

		return null;
	}

	/**
	 * Given an internal code, return the corresponding HpoTerm or throw an exception.
	 * 
	 * @param code
	 * @return the term with negation
	 * @throws UnmappedInternalCodeException
	 */
	private HpoTermWithNegation getHpoTermForInternalCode(Loinc2HpoCodedValue code)
			throws ConversionException {
		HpoTermWithNegation term = codeToHpoTerm.get(code);
		if (term == null) {
			throw new ConversionException(ConversionExceptionType.UNMAPPED_INTERNAL_CODE, "The internal code " + code.name() + " has no HPO mapping for LOINC " + loincId.getCode());
		}
		return term;
	}

	private HpoTermWithNegation getHpoTermForValueQuantity(Quantity valueQuantity,
			List<ObservationReferenceRangeComponent> referenceRange)
			throws ConversionException {
		if (referenceRange.size() < 1) {
			throw new ConversionException(ConversionExceptionType.REFERENCE_RANGE_NOT_FOUND);
		} else if (referenceRange.size() > 1) {
			// TODO: It can happen when there is actually one range but coded in three ranges
			// e.g. normal 20-30
			// in this case, one range ([20, 30]) is sufficient;
			// however, it is written as three ranges: ( , 20) [20, 30] (30, )
			// We should handle this case
			throw new ConversionException(ConversionExceptionType.AMBIGUOUS_REFERENCE_RANGE);
		}

		ObservationReferenceRangeComponent targetReference = referenceRange.get(0);
		double low = targetReference.hasLow() ? targetReference.getLow().getValue().doubleValue() : Double.MIN_VALUE;
		double high = targetReference.hasHigh() ? targetReference.getHigh().getValue().doubleValue() : Double.MAX_VALUE;
		double observed = valueQuantity.getValue().doubleValue();
		Loinc2HpoCodedValue result;
		if (observed < low) {
			result = Loinc2HpoCodedValue.L;
		} else if (observed > high) {
			result = Loinc2HpoCodedValue.H;
		} else {
			result = Loinc2HpoCodedValue.N;
		}
		return getHpoTermForInternalCode(result);
	}

}
