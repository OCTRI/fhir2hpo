package org.monarchinitiative.fhir2hpo.loinc;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.Observation;
import org.hl7.fhir.r5.model.Observation.ObservationReferenceRangeComponent;
import org.hl7.fhir.r5.model.Quantity;
import org.monarchinitiative.fhir2hpo.codesystems.CodeContainer;
import org.monarchinitiative.fhir2hpo.codesystems.CodeableConceptAnalyzer;
import org.monarchinitiative.fhir2hpo.codesystems.HpoEncodedValue;
import org.monarchinitiative.fhir2hpo.fhir.util.ObservationLoincInfo;
import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;
import org.monarchinitiative.fhir2hpo.hpo.LoincConversionResult;
import org.monarchinitiative.fhir2hpo.hpo.MethodConversionResult;
import org.monarchinitiative.fhir2hpo.loinc.exception.AmbiguousReferenceRangeException;
import org.monarchinitiative.fhir2hpo.loinc.exception.ConversionException;
import org.monarchinitiative.fhir2hpo.loinc.exception.MismatchedLoincIdException;
import org.monarchinitiative.fhir2hpo.loinc.exception.MissingInterpretationException;
import org.monarchinitiative.fhir2hpo.loinc.exception.MissingValueQuantityException;
import org.monarchinitiative.fhir2hpo.loinc.exception.MissingValueStringException;
import org.monarchinitiative.fhir2hpo.loinc.exception.ReferenceRangeNotFoundException;
import org.monarchinitiative.fhir2hpo.loinc.exception.UnmappedInternalCodeException;
import org.monarchinitiative.fhir2hpo.loinc.exception.UnmappedValueStringException;

/**
 * This represents the default annotation implementation where a single observation is parsed
 * to determine the HPO mapping. It will try all the following approaches:
 * - Find HPO through interpretation CodeableConcept
 * - Find HPO through value quantity
 * - Find HPO through value string
 * 
 * @author yateam
 *
 */
public class DefaultLoinc2HpoAnnotation implements Loinc2HpoAnnotation {

	private final LoincId loincId;
	private final LoincScale loincScale;
	// Map from a coded value to term including negation
	private final Map<HpoEncodedValue, HpoTermWithNegation> codeToHpoTerm;

	public static class Builder {

		private LoincId loincId = null;
		private LoincScale loincScale = null;
		private final Map<HpoEncodedValue, HpoTermWithNegation> codeToHpoTerm = new HashMap<>();

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
		 * Add a new mapping from a coded value to an HpoTermWithNegation
		 * 
		 * @param codedValue
		 * @param term
		 * @return
		 */
		public Builder addMapping(HpoEncodedValue codedValue, HpoTermWithNegation term) {
			this.codeToHpoTerm.put(codedValue, term);
			return this;
		}

		public DefaultLoinc2HpoAnnotation build() {
			return new DefaultLoinc2HpoAnnotation(loincId,
				loincScale,
				codeToHpoTerm);
		}
	}

	private DefaultLoinc2HpoAnnotation(LoincId loincId, LoincScale loincScale,
		Map<HpoEncodedValue, HpoTermWithNegation> codeToHpoTerm) {
		this.loincId = loincId;
		this.loincScale = loincScale;
		this.codeToHpoTerm = codeToHpoTerm;

		// If a "normal" term is mapped but not an "abnormal" term, create one.
		if (codeToHpoTerm.containsKey(HpoEncodedValue.NORMAL) && !codeToHpoTerm.containsKey(HpoEncodedValue.ABNORMAL)) {
			HpoTermWithNegation normalTerm = codeToHpoTerm.get(HpoEncodedValue.NORMAL);
			codeToHpoTerm.put(HpoEncodedValue.ABNORMAL,
				new HpoTermWithNegation(normalTerm.getHpoTermId(), !normalTerm.isNegated()));
		}
	}

	/**
	 * 
	 * @return the LOINC Id
	 */
	@Override
	public LoincId getLoincId() {
		return loincId;
	}

	/**
	 * 
	 * @return the LOINC scale
	 */
	@Override
	public LoincScale getLoincScale() {
		return loincScale;
	}

	@Override
	public Collection<HpoTermWithNegation> getHpoTerms() {
		return codeToHpoTerm.values();
	}

	@Override
	public LoincConversionResult convert(Observation observation) {
		
		LoincConversionResult result = new LoincConversionResult(loincId);
		try {
			
			ObservationLoincInfo observationLoincInfo = new ObservationLoincInfo(loincId, observation);
			result.setObservationLoincInfo(observationLoincInfo);
			
			result.addMethodConversionResult(convertInterpretation(observationLoincInfo.getInterpretation()));
			result.addMethodConversionResult(convertValueQuantity(observationLoincInfo.getValueQuantity(), observationLoincInfo.getReferenceRange()));
			result.addMethodConversionResult(convertValueString(observationLoincInfo.getValueString()));
			
			// ValueCodeableConcept might apply to Ord LOINCs. However, in examples we've seen the concept
			// is encoded using a different system (e.g., SNOMED) so we would need to represent annotations
			// in an advanced way. See: https://www.hl7.org/fhir/observation-example-f206-staphylococcus.json

		} catch (MismatchedLoincIdException e) {
			result.setException(e);
		}

		return result;
	}

	private MethodConversionResult convertInterpretation(Optional<CodeableConcept> interpretation) {
		MethodConversionResult result = new MethodConversionResult("Interpretation");
		try {
			if (interpretation.isPresent()) {
				HpoEncodedValue internalCode = CodeableConceptAnalyzer
					.getInternalCodeForCodeableConcept(interpretation.get());
				result.succeed(getHpoTermForInternalCode(internalCode));				
			} else {
				throw new MissingInterpretationException();
			}
		} catch (ConversionException e) {
			result.fail(e);
		}
		
		return result;
	}

	private MethodConversionResult convertValueQuantity(Optional<Quantity> valueQuantity, Optional<List<ObservationReferenceRangeComponent>> referenceRange) {
		MethodConversionResult result = new MethodConversionResult("ValueQuantity");
		try {
			if (valueQuantity.isPresent()) {
				HpoTermWithNegation hpoTerm = getHpoTermForValueQuantity(valueQuantity.get(),
					referenceRange);
				result.succeed(hpoTerm);
			} else {
				throw new MissingValueQuantityException();
			}
		} catch (Exception e) {
			result.fail(e);
		}
		return result;
	}
	
	private MethodConversionResult convertValueString(Optional<String> valueString) {
		MethodConversionResult result = new MethodConversionResult("ValueString");
		try {
			if (valueString.isPresent()) {
				// Normalize the value string and create a Coding to check against the CodeContainer mapping
				Coding coding = new Coding(CodeContainer.CODING_VALUE_STRING,
					valueString.get().toLowerCase().trim(),
					CodeContainer.CODING_VALUE_STRING);
				HpoEncodedValue internalCode = CodeContainer.getInternalCode(coding);
				if (internalCode == null) {
					throw new UnmappedValueStringException();
				} else {
					HpoTermWithNegation hpoTerm = getHpoTermForInternalCode(internalCode);
					result.succeed(hpoTerm);
				}
			} else {
				throw new MissingValueStringException();
			}
		} catch (Exception e) {
			result.fail(e);
		}
		return result;
	}



	/**
	 * Given an HpoEncodedValue, returns the corresponding HpoTerm or throws an exception.
	 *
	 * @param code
	 * @return the term with negation
	 * @throws UnmappedInternalCodeException
	 */
	private HpoTermWithNegation getHpoTermForInternalCode(HpoEncodedValue code)
		throws UnmappedInternalCodeException {
		HpoTermWithNegation term = codeToHpoTerm.get(code);
		if (term == null) {
			throw new UnmappedInternalCodeException(
				"The code " + code.getCoding().getCode() + " has no HPO mapping for LOINC " + loincId.getCode());
		}
		return term;
	}

	/**
	 * Given a ValueQuantity and a ReferenceRange, try to convert to an Hpo Term.
	 * 
	 * @param valueQuantity
	 * @param referenceRange
	 * @return
	 * @throws ConversionException
	 */
	private HpoTermWithNegation getHpoTermForValueQuantity(Quantity valueQuantity,
		Optional<List<ObservationReferenceRangeComponent>> referenceRange)
		throws ConversionException {
		if (!referenceRange.isPresent()) {
			throw new ReferenceRangeNotFoundException();
		} else if (referenceRange.get().size() > 1) {
			// TODO: It can happen when there is actually one range but coded in three ranges
			// e.g. normal 20-30
			// in this case, one range ([20, 30]) is sufficient;
			// however, it is written as three ranges: ( , 20) [20, 30] (30, )
			// We should handle this case
			throw new AmbiguousReferenceRangeException();
		}

		ObservationReferenceRangeComponent targetReference = referenceRange.get().get(0);
		double low = targetReference.hasLow() ? targetReference.getLow().getValue().doubleValue() : Double.MIN_VALUE;
		double high = targetReference.hasHigh() ? targetReference.getHigh().getValue().doubleValue() : Double.MAX_VALUE;
		double observed = valueQuantity.getValue().doubleValue();
		HpoEncodedValue result;
		if (observed < low) {
			result = HpoEncodedValue.LOW;
		} else if (observed > high) {
			result = HpoEncodedValue.HIGH;
		} else {
			result = HpoEncodedValue.NORMAL;
		}
		return getHpoTermForInternalCode(result);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(loincId.toString() + ": {");
		sb.append(codeToHpoTerm.entrySet().stream().map(set -> set.toString()).collect(Collectors.joining(",")));
		sb.append("}");
		return sb.toString();
	}

}
