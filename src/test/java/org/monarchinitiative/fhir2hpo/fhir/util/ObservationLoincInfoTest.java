package org.monarchinitiative.fhir2hpo.fhir.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.hl7.fhir.dstu3.model.Observation;
import org.junit.Test;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.exception.ConversionException;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincException;
import org.monarchinitiative.fhir2hpo.loinc.exception.MismatchedLoincIdException;
import org.monarchinitiative.fhir2hpo.util.FhirParseUtils;

public class ObservationLoincInfoTest {

	@Test(expected=MismatchedLoincIdException.class)
	public void testMismatchedLoincId() throws LoincException, ConversionException {
		Observation observation = FhirParseUtils.getObservation("fhir/observation/basMetab1998Panel.json");
		new ObservationLoincInfo(new LoincId("999-9"), observation);		
	}

	@Test
	public void testInfoInCodeSection() throws LoincException, ConversionException {
		Observation observation = FhirParseUtils.getObservation("fhir/observation/basMetab1998Panel.json");
		ObservationLoincInfo info = new ObservationLoincInfo(new LoincId("24320-4"), observation);
		assertEquals("The fhirId is extracted correctly.", "bas-metab-1998", info.getFhirId());
		assertEquals("The effectiveDateTime is extracted correctly", "2012-09-17", info.getDate());
		assertEquals("The code text is used for the description", "Bas Metab Panel with Annotated Components", info.getDescription());
		assertNull("There is no value description for the code.", info.getValueDescription());
		assertFalse("There is no interpretation for the code", info.getInterpretation().isPresent());
		assertFalse("There is no value quantity for the code", info.getValueQuantity().isPresent());
		assertFalse("There is no value string for the code", info.getValueString().isPresent());
		assertFalse("There is no reference range for the code", info.getReferenceRange().isPresent());		
	}

	@Test
	public void testValueQuantityInfoInComponentSection() throws LoincException, ConversionException {
		Observation observation = FhirParseUtils.getObservation("fhir/observation/basMetab1998Panel.json");
		ObservationLoincInfo info = new ObservationLoincInfo(new LoincId("2160-0"), observation);
		assertEquals("The fhirId is extracted correctly.", "bas-metab-1998", info.getFhirId());
		assertEquals("The effectiveDateTime is extracted correctly", "2012-09-17", info.getDate());
		assertEquals("The component code display is used for the description", "Creat SerPl-mCnc", info.getDescription());
		assertEquals("The value description for the component includes the quantity and unit", "1.0 mg/dL", info.getValueDescription());
		assertFalse("There is no interpretation for the component", info.getInterpretation().isPresent());
		assertTrue("There is a value quantity for the component", info.getValueQuantity().isPresent());
		assertFalse("There is no value string for the component", info.getValueString().isPresent());
		assertTrue("There is a reference range for the component", info.getReferenceRange().isPresent());		
	}

	@Test
	public void testInterpretationInfoInComponentSection() throws LoincException, ConversionException {
		Observation observation = FhirParseUtils.getObservation("fhir/observation/basMetab1998Panel.json");
		ObservationLoincInfo info = new ObservationLoincInfo(new LoincId("2345-7"), observation);
		assertEquals("The fhirId is extracted correctly.", "bas-metab-1998", info.getFhirId());
		assertEquals("The effectiveDateTime is extracted correctly", "2012-09-17", info.getDate());
		assertEquals("The component code display is used for the description", "Glucose SerPl-mCnc", info.getDescription());
		assertEquals("The value description for the component includes the quantity and unit", "6.3 mmol/l", info.getValueDescription());
		assertTrue("There is an interpretation for the component", info.getInterpretation().isPresent());
		assertTrue("There is a value quantity for the component", info.getValueQuantity().isPresent());
		assertFalse("There is no value string for the component", info.getValueString().isPresent());
		assertFalse("There is no reference range for the component", info.getReferenceRange().isPresent());		
	}

}
