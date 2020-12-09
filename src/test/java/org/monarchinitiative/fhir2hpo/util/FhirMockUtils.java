package org.monarchinitiative.fhir2hpo.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Coding;

public class FhirMockUtils {
	
	public static CodeableConcept mockCodeableConcept(List<Coding> codings) {
		CodeableConcept codeableConcept = mock(CodeableConcept.class);
		when(codeableConcept.getCoding()).thenReturn(codings);
		return codeableConcept;
	}
	
	public static Coding mockCoding(String system, String code) {
		Coding coding = mock(Coding.class);
		when(coding.getSystem()).thenReturn(system);
		when(coding.getCode()).thenReturn(code);
		return coding;
	}

}
