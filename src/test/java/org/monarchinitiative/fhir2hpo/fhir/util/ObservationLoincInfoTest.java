package org.monarchinitiative.fhir2hpo.fhir.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Period;
import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.exception.ConversionException;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincException;
import org.monarchinitiative.fhir2hpo.loinc.exception.MismatchedLoincIdException;
import org.monarchinitiative.fhir2hpo.util.FhirParseUtils;

public class ObservationLoincInfoTest {
	
	private LoincId panelLoinc;
	private LoincId creatLoinc;
	private LoincId glucoseLoinc;
	private Date effectiveDateTime;
	private Date periodStart;
	private Date periodEnd;
	
	@Before
	public void setup() throws ParseException, LoincException {
		panelLoinc = new LoincId("24320-4");
		creatLoinc = new LoincId("2160-0");
		glucoseLoinc = new LoincId("2345-7");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		effectiveDateTime = df.parse("2012-09-17");
		periodStart = df.parse("2012-01-01");
		periodEnd = df.parse("2012-12-31");		
	}

	@Test(expected=MismatchedLoincIdException.class)
	public void testMismatchedLoincId() throws LoincException, ConversionException {
		Observation observation = FhirParseUtils.getObservation("fhir/observation/basMetab1998Panel.json");
		new ObservationLoincInfo(new LoincId("999-9"), observation);		
	}

	@Test
	public void testInfoInCodeSection() throws ConversionException {
		Observation observation = FhirParseUtils.getObservation("fhir/observation/basMetab1998Panel.json");
		ObservationLoincInfo info = new ObservationLoincInfo(panelLoinc, observation);
		assertEquals("The fhirId is extracted correctly.", "bas-metab-1998", info.getFhirId());
		assertEquals("The start date is extracted correctly", effectiveDateTime, info.getStartDate().get());
		assertEquals("The end date is extracted correctly", effectiveDateTime, info.getEndDate().get());
		assertEquals("The code text is used for the description", "Bas Metab Panel with Annotated Components", info.getDescription());
		assertNull("There is no value description for the code.", info.getValueDescription());
		assertFalse("There is no interpretation for the code", info.getInterpretation().isPresent());
		assertFalse("There is no value quantity for the code", info.getValueQuantity().isPresent());
		assertFalse("There is no value string for the code", info.getValueString().isPresent());
		assertFalse("There is no reference range for the code", info.getReferenceRange().isPresent());		
	}

	@Test
	public void testValueQuantityInfoInComponentSection() throws ConversionException {
		Observation observation = FhirParseUtils.getObservation("fhir/observation/basMetab1998Panel.json");
		ObservationLoincInfo info = new ObservationLoincInfo(creatLoinc, observation);
		assertEquals("The fhirId is extracted correctly.", "bas-metab-1998", info.getFhirId());
		assertEquals("The start date is extracted correctly", effectiveDateTime, info.getStartDate().get());
		assertEquals("The end date is extracted correctly", effectiveDateTime, info.getEndDate().get());
		assertEquals("The component code display is used for the description", "Creat SerPl-mCnc", info.getDescription());
		assertEquals("The value description for the component includes the quantity and unit", "1.0 mg/dL", info.getValueDescription());
		assertFalse("There is no interpretation for the component", info.getInterpretation().isPresent());
		assertTrue("There is a value quantity for the component", info.getValueQuantity().isPresent());
		assertFalse("There is no value string for the component", info.getValueString().isPresent());
		assertTrue("There is a reference range for the component", info.getReferenceRange().isPresent());		
	}

	@Test
	public void testInterpretationInfoInComponentSection() throws ConversionException {
		Observation observation = FhirParseUtils.getObservation("fhir/observation/basMetab1998Panel.json");
		ObservationLoincInfo info = new ObservationLoincInfo(glucoseLoinc, observation);
		assertEquals("The fhirId is extracted correctly.", "bas-metab-1998", info.getFhirId());
		assertEquals("The start date is extracted correctly", effectiveDateTime, info.getStartDate().get());
		assertEquals("The end date is extracted correctly", effectiveDateTime, info.getEndDate().get());
		assertEquals("The component code display is used for the description", "Glucose SerPl-mCnc", info.getDescription());
		assertEquals("The value description for the component includes the quantity and unit", "6.3 mmol/l", info.getValueDescription());
		assertTrue("There is an interpretation for the component", info.getInterpretation().isPresent());
		assertTrue("There is a value quantity for the component", info.getValueQuantity().isPresent());
		assertFalse("There is no value string for the component", info.getValueString().isPresent());
		assertFalse("There is no reference range for the component", info.getReferenceRange().isPresent());		
	}
	
	@Test
	public void testEffectivePeriod() throws ConversionException {
		// Tweak the observation to have a period instead of a datetime
		Observation observation = FhirParseUtils.getObservation("fhir/observation/basMetab1998Panel.json");
		Period period = new Period();
		period.setStart(periodStart);
		period.setEnd(periodEnd);
		observation.setEffective(period);
		
		ObservationLoincInfo info = new ObservationLoincInfo(panelLoinc, observation);
		assertEquals("The start date is extracted correctly", periodStart, info.getStartDate().get());
		assertEquals("The end date is extracted correctly", periodEnd, info.getEndDate().get());
		
		// Test period without a start date
		period = new Period();
		period.setEnd(periodEnd);
		observation.setEffective(period);
		
		info = new ObservationLoincInfo(panelLoinc, observation);
		assertFalse("The start date is not present", info.getStartDate().isPresent());
		assertEquals("The end date is extracted correctly", periodEnd, info.getEndDate().get());
		
	}
	
	@Test
	public void testObservationWithoutDate() throws ConversionException {
		// Tweak the observation to have no effective value
		Observation observation = FhirParseUtils.getObservation("fhir/observation/basMetab1998Panel.json");
		observation.setEffective(null);
		
		ObservationLoincInfo info = new ObservationLoincInfo(panelLoinc, observation);
		assertFalse("The start date is not present", info.getStartDate().isPresent());
		assertFalse("The end date is not present", info.getEndDate().isPresent());
		
	}

}
