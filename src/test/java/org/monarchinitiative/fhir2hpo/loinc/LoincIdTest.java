package org.monarchinitiative.fhir2hpo.loinc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincException;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincException.LoincExceptionType;

public class LoincIdTest {
	
    @Test
    public void testConstructor() throws LoincException {
        String code = "15074-8";
        LoincId id = new LoincId(code);
        assertEquals(code,id.getCode());
        
        code = "3141-9";
        id = new LoincId(code);
        assertEquals(code,id.getCode());
    }

    public void testMalformedCode() {
        String code = "15074-";
        
		try {
			new LoincId(code);
		} catch (LoincException e) {
	        assertEquals("Expected malformed loinc exception", LoincExceptionType.MALFORMED_LOINC_CODE, e.getType());
		}
    }

    public void testMalformedCode2() {
        String code = "1507423";
        
		try {
			new LoincId(code);
		} catch (LoincException e) {
	        assertEquals("Expected malformed loinc exception", LoincExceptionType.MALFORMED_LOINC_CODE, e.getType());
		}
    }

    @Test
    public void testEquals() throws LoincException {
        String code1="19048-8";
        String code2="19048-8";
        LoincId id1=new LoincId(code1);
        LoincId id2=new LoincId(code2);
        assertEquals(id1,id2);
    }

}
