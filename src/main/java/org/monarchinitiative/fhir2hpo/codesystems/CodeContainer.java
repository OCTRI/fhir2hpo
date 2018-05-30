package org.monarchinitiative.fhir2hpo.codesystems;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hl7.fhir.dstu3.model.Coding;

//TODO: Consider allowing jar user to add their own mappings as original library did
public class CodeContainer {
	
	private static Map<String,Loinc2HpoCodedValue> fhirv2interpretations = Collections.unmodifiableMap(Stream.of(
			new SimpleEntry<>("<", Loinc2HpoCodedValue.L),
			new SimpleEntry<>(">", Loinc2HpoCodedValue.H),
			new SimpleEntry<>("A", Loinc2HpoCodedValue.A),
			new SimpleEntry<>("AA", Loinc2HpoCodedValue.A),
			new SimpleEntry<>("AC", Loinc2HpoCodedValue.H),
			new SimpleEntry<>("B", Loinc2HpoCodedValue.N),
			new SimpleEntry<>("D", Loinc2HpoCodedValue.L),
			new SimpleEntry<>("DET", Loinc2HpoCodedValue.H),
			new SimpleEntry<>("H", Loinc2HpoCodedValue.H),
			new SimpleEntry<>("HH", Loinc2HpoCodedValue.H),
			new SimpleEntry<>("HM", Loinc2HpoCodedValue.U),
			new SimpleEntry<>("HU", Loinc2HpoCodedValue.H),
			new SimpleEntry<>("I", Loinc2HpoCodedValue.N),
			new SimpleEntry<>("IE", Loinc2HpoCodedValue.U),
			new SimpleEntry<>("IND", Loinc2HpoCodedValue.U),
			new SimpleEntry<>("L", Loinc2HpoCodedValue.L),
			new SimpleEntry<>("LL", Loinc2HpoCodedValue.L),
			new SimpleEntry<>("LU", Loinc2HpoCodedValue.L),
			new SimpleEntry<>("MS", Loinc2HpoCodedValue.U),
			new SimpleEntry<>("N", Loinc2HpoCodedValue.N),
			new SimpleEntry<>("ND", Loinc2HpoCodedValue.N),
			new SimpleEntry<>("NEG", Loinc2HpoCodedValue.N),
			new SimpleEntry<>("NR", Loinc2HpoCodedValue.N),
			new SimpleEntry<>("NS", Loinc2HpoCodedValue.U),
			new SimpleEntry<>("null", Loinc2HpoCodedValue.U),
			new SimpleEntry<>("OBX", Loinc2HpoCodedValue.U),
			new SimpleEntry<>("POS", Loinc2HpoCodedValue.H),
			new SimpleEntry<>("QCF", Loinc2HpoCodedValue.U),
			new SimpleEntry<>("R", Loinc2HpoCodedValue.U),
			new SimpleEntry<>("RR", Loinc2HpoCodedValue.H),
			new SimpleEntry<>("S", Loinc2HpoCodedValue.U),
			new SimpleEntry<>("SDD", Loinc2HpoCodedValue.U),
			new SimpleEntry<>("SYN-R", Loinc2HpoCodedValue.U),
			new SimpleEntry<>("SYN-S", Loinc2HpoCodedValue.U),
			new SimpleEntry<>("TOX", Loinc2HpoCodedValue.H),
			new SimpleEntry<>("U", Loinc2HpoCodedValue.H),
			new SimpleEntry<>("VS", Loinc2HpoCodedValue.U),
			new SimpleEntry<>("W", Loinc2HpoCodedValue.A),
			new SimpleEntry<>("WR", Loinc2HpoCodedValue.H))
            .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));

    // Map from the code system (e.g., http://hl7.org/fhir/v2/0078) to the external code for that system to the corresponding internal code
    private static Map<String, Map<String, Loinc2HpoCodedValue>> codelists = Collections.unmodifiableMap(Stream.of(            
    		new SimpleEntry<>("http://hl7.org/fhir/v2/0078", fhirv2interpretations))
            .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));
    
    /**
     * Get the internal code that maps to the external coding.
     * @param coding the external coding
     * @return the internal code or null if either the system or code is not found
     */
    public static Loinc2HpoCodedValue getInternalCode(Coding coding) {
    	Map<String, Loinc2HpoCodedValue> systemMap = codelists.get(coding.getSystem());
    	
    	if (systemMap != null) {
    		return systemMap.get(coding.getCode());
    	}
    	
    	return null;
    }

}
