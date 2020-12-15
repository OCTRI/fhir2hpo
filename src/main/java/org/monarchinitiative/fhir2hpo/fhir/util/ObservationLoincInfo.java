package org.monarchinitiative.fhir2hpo.fhir.util;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.DataType;
import org.hl7.fhir.r5.model.DateTimeType;
import org.hl7.fhir.r5.model.Observation;
import org.hl7.fhir.r5.model.Observation.ObservationComponentComponent;
import org.hl7.fhir.r5.model.Observation.ObservationReferenceRangeComponent;
import org.hl7.fhir.r5.model.Period;
import org.hl7.fhir.r5.model.Quantity;
import org.hl7.fhir.r5.model.StringType;
import org.hl7.fhir.exceptions.FHIRException;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.exception.MismatchedLoincIdException;

/**
 * This class extracts elements of interest from a FHIR observation for a specific LOINC, regardless of whether it is
 * in the code section or a component.
 * 
 * @author yateam
 *
 */
public class ObservationLoincInfo {

	private String fhirId;
	private LoincId loincId;
	private String description;
	private Optional<Date> startDate = Optional.empty();
	private Optional<Date> endDate = Optional.empty();
	private String valueDescription;
	private Optional<CodeableConcept> interpretation = Optional.empty();
	private Optional<Quantity> valueQuantity = Optional.empty();
	private Optional<List<ObservationReferenceRangeComponent>> referenceRange = Optional.empty();
	private Optional<String> valueString = Optional.empty();

	public ObservationLoincInfo(LoincId loincId, Observation observation) throws MismatchedLoincIdException {
		
		this.fhirId = observation.getIdElement().getIdPart();
		this.loincId = loincId;
		setDate(observation);
		boolean containsLoinc = false;
		if (ObservationUtil.getCodeSectionLoincIdsOfObservation(observation).contains(loincId)) {
			containsLoinc = true;
			setFieldsWithReflection(observation);
		} else {
			ObservationComponentComponent component = ObservationUtil.getComponentLoincIdsOfObservation(observation)
				.get(loincId);
			if (component != null) {
				containsLoinc = true;
				setFieldsWithReflection(component);
			}

		}
		
		if (!containsLoinc) {
			throw new MismatchedLoincIdException("The observation does not contain the LOINC " + loincId);
		}
	}

	private void setDate(Observation observation) {
		try {
			if (observation.hasEffective()) {
				DataType effective = observation.getEffective();
				if (effective instanceof DateTimeType) {
					// Set start and end date to the same
					startDate = Optional.of(observation.getEffectiveDateTimeType().getValue());
					endDate = startDate;
				} else if (effective instanceof Period) {
					Period period = observation.getEffectivePeriod();
					if (period.hasStart()) {
						startDate = Optional.of(period.getStart());
					}
					if (period.hasEnd()) {
						endDate = Optional.of(period.getEnd());
					}
				}
			}
		} catch (FHIRException e) {
			// This should not occur since we check existence before getting
			e.printStackTrace();
		}
	}
	
	/**
	 * Call getters on either the Observation or the ObservationComponentComponent depending
	 * on where the LOINC of interest lives. Set fields of interest.
	 * @param the observation or the component of it relevant to the LOINC
	 */
	private void setFieldsWithReflection(Object o) {
		Class<? extends Object> clazz = o.getClass();
		try {
			
			// Call getCode() on the object and try to extract a description, falling back on the LOINC if one is not found
			CodeableConcept code = (CodeableConcept) clazz.getDeclaredMethod("getCode").invoke(o);
			description = ObservationUtil.getDescriptionOfCodeableConcept(code);
			if (description == null) {
				description = loincId.toString();
			}
			
			// Call hasInterpretation(). If true, set the Optional interpretation field
			if ((boolean) clazz.getDeclaredMethod("hasInterpretation").invoke(o)) {
				// TODO: AEY R4 changes the return value to a list. For now just get the first.
				List<CodeableConcept> interpretations = (List<CodeableConcept>) clazz.getDeclaredMethod("getInterpretation").invoke(o);
				interpretation = Optional.of(interpretations.get(0));
			}
			
			// Call getValue(). If value exists, set the Optional ValueQuantity and ValueString fields. Other Types are ignored.
			DataType value = (DataType) clazz.getDeclaredMethod("getValue").invoke(o);
			if (value != null) {
				if (value instanceof Quantity) {
					Quantity quantity = (Quantity) clazz.getDeclaredMethod("getValueQuantity").invoke(o);
					valueQuantity = Optional.of(quantity);
					valueDescription = valueQuantity.get().getValue() + " " + valueQuantity.get().getUnit();
				} else if (value instanceof StringType) {
					StringType stringType = (StringType) clazz.getDeclaredMethod("getValueStringType").invoke(o);
					valueString = Optional.of(stringType.asStringValue());
					valueDescription = valueString.get();
				} 
				// No other value types supported
			}
			
			// Call hasReferenceRange(). If true, set the Optional reference range field
			if ((boolean) clazz.getDeclaredMethod("hasReferenceRange").invoke(o)) {
				referenceRange = Optional.of((List<ObservationReferenceRangeComponent>) clazz.getDeclaredMethod("getReferenceRange").invoke(o));
			}
			
		} catch (Exception e) {
			// Some of the invoked methods can throw exceptions. They shouldn't, but just in case we catch
			// all general exceptions here
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the identifier of the original observation
	 * @return
	 */
	public String getFhirId() {
		return fhirId;
	}

	/**
	 * Get the LOINC of interest within the observation
	 * @return
	 */
	public LoincId getLoincId() {
		return loincId;
	}

	/**
	 * Get a text description of the LOINC if one can be found, otherwise return the LOINC
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Get the start date
	 * @return
	 */
	public Optional<Date> getStartDate() {
		return startDate;
	}

	/**
	 * Get the end date
	 * @return
	 */
	public Optional<Date> getEndDate() {
		return endDate;
	}

	/**
	 * Gets a string representation of the value if it can be determined. The following
	 * Types are supported:
	 * - ValueQuantity -> decimal value + unit
	 * - ValueString -> string
	 *  
	 * @return the value as a string, null otherwise
	 */
	public String getValueDescription() {
		return valueDescription;
	}

	/**
	 * Get the Interpretation corresponding to the LOINC
	 * @return
	 */
	public Optional<CodeableConcept> getInterpretation() {
		return interpretation;
	}

	/**
	 * Get the Quantity corresponding to the LOINC
	 * @return
	 */
	public Optional<Quantity> getValueQuantity() {
		return valueQuantity;
	}

	/**
	 * Get the reference range corresponding to the LOINC
	 * @return
	 */
	public Optional<List<ObservationReferenceRangeComponent>> getReferenceRange() {
		return referenceRange;
	}

	/**
	 * Get the ValueString corresponding to the LOINC. Note that this is the ValueString on
	 * the FHIR Observation or Component if it exists and is not equivalent to getValueDescription()
	 * @return
	 */
	public Optional<String> getValueString() {
		return valueString;
	}


}
