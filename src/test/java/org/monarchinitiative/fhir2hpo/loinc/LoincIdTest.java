package org.monarchinitiative.fhir2hpo.loinc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.monarchinitiative.fhir2hpo.loinc.exception.MalformedLoincCodeException;

public class LoincIdTest {
	
    @Test
    public void testConstructor() throws MalformedLoincCodeException {
        String code = "15074-8";
        LoincId id = new LoincId(code);
        assertEquals(code,id.getCode());
        
        code = "3141-9";
        id = new LoincId(code);
        assertEquals(code,id.getCode());
    }

    @Test(expected = MalformedLoincCodeException.class)
    public void testMalformedCode() throws MalformedLoincCodeException {
        String code = "15074-";
        LoincId id = new LoincId(code);
        assertEquals(code,id.getCode());
    }

    @Test(expected = MalformedLoincCodeException.class)
    public void testMalformedCode2() throws MalformedLoincCodeException {
        String code = "1507423";
        LoincId id = new LoincId(code);
        assertEquals(code,id.getCode());
    }

    @Test
    public void testEquals() throws MalformedLoincCodeException {
        String code1="19048-8";
        String code2="19048-8";
        LoincId id1=new LoincId(code1);
        LoincId id2=new LoincId(code2);
        assertEquals(id1,id2);
    }

}
