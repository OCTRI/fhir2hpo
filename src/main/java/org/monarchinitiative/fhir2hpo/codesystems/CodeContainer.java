package org.monarchinitiative.fhir2hpo.codesystems;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hl7.fhir.dstu3.model.Coding;

//TODO: Consider allowing jar user to add their own mappings as original library did
public class CodeContainer {
	
	private static Map<String,String> fhirv2interpretations = Collections.unmodifiableMap(Stream.of(
			new SimpleEntry<>("<", "L"),
			new SimpleEntry<>(">", "H"),
			new SimpleEntry<>("A", "A"),
			new SimpleEntry<>("AA", "A"),
			new SimpleEntry<>("AC", "POS"),
			new SimpleEntry<>("B", "N"),
			new SimpleEntry<>("D", "L"),
			new SimpleEntry<>("DET", "POS"),
			new SimpleEntry<>("H", "H"),
			new SimpleEntry<>("HH", "H"),
			new SimpleEntry<>("HM", "U"),
			new SimpleEntry<>("HU", "H"),
			new SimpleEntry<>("I", "N"),
			new SimpleEntry<>("IE", "U"),
			new SimpleEntry<>("IND", "U"),
			new SimpleEntry<>("L", "L"),
			new SimpleEntry<>("LL", "L"),
			new SimpleEntry<>("LU", "L"),
			new SimpleEntry<>("MS", "U"),
			new SimpleEntry<>("N", "N"),
			new SimpleEntry<>("ND", "NEG"),
			new SimpleEntry<>("NEG", "NEG"),
			new SimpleEntry<>("NR", "NEG"),
			new SimpleEntry<>("NS", "U"),
			new SimpleEntry<>("null", "U"),
			new SimpleEntry<>("OBX", "U"),
			new SimpleEntry<>("POS", "POS"),
			new SimpleEntry<>("QCF", "U"),
			new SimpleEntry<>("R", "U"),
			new SimpleEntry<>("RR", "POS"),
			new SimpleEntry<>("S", "U"),
			new SimpleEntry<>("SDD", "U"),
			new SimpleEntry<>("SYN-R", "U"),
			new SimpleEntry<>("SYN-S", "U"),
			new SimpleEntry<>("TOX", "POS"),
			new SimpleEntry<>("U", "H"),
			new SimpleEntry<>("VS", "U"),
			new SimpleEntry<>("W", "A"),
			new SimpleEntry<>("WR", "POS"))
            .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));

    // Map from the code system (e.g., http://hl7.org/fhir/v2/0078) to the external code for that system to the corresponding internal code
    private static Map<String, Map<String, String>> codelists = Collections.unmodifiableMap(Stream.of(            
    		new SimpleEntry<>("http://hl7.org/fhir/v2/0078", fhirv2interpretations))
            .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));
    
    /**
     * Get the internal code that maps to the external coding.
     * @param coding the external coding
     * @return the internal code or null if either the system or code is not found
     */
    public static String getInternalCode(Coding coding) {
    	Map<String, String> systemMap = codelists.get(coding.getSystem());
    	
    	if (systemMap != null) {
    		return systemMap.get(coding.getCode());
    	}
    	
    	return null;
    }

}
