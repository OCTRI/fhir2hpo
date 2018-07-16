package org.monarchinitiative.fhir2hpo.codesystems;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hl7.fhir.dstu3.model.Coding;

//TODO: Consider allowing jar user to add their own mappings as original library did
public class CodeContainer {
	
	public static final String CODING_VALUE_STRING = "valueString";
	
	private static Map<String,HpoEncodedValue> fhirv2interpretations = Collections.unmodifiableMap(Stream.of(
			new SimpleEntry<>("<", HpoEncodedValue.LOW),
			new SimpleEntry<>(">", HpoEncodedValue.HIGH),
			new SimpleEntry<>("A", HpoEncodedValue.ABNORMAL),
			new SimpleEntry<>("AA", HpoEncodedValue.ABNORMAL),
			new SimpleEntry<>("AC", HpoEncodedValue.HIGH),
			new SimpleEntry<>("B", HpoEncodedValue.NORMAL),
			new SimpleEntry<>("D", HpoEncodedValue.LOW),
			new SimpleEntry<>("DET", HpoEncodedValue.HIGH),
			new SimpleEntry<>("H", HpoEncodedValue.HIGH),
			new SimpleEntry<>("HH", HpoEncodedValue.HIGH),
			new SimpleEntry<>("HM", HpoEncodedValue.UNKNOWN),
			new SimpleEntry<>("HU", HpoEncodedValue.HIGH),
			new SimpleEntry<>("I", HpoEncodedValue.NORMAL),
			new SimpleEntry<>("IE", HpoEncodedValue.UNKNOWN),
			new SimpleEntry<>("IND", HpoEncodedValue.UNKNOWN),
			new SimpleEntry<>("L", HpoEncodedValue.LOW),
			new SimpleEntry<>("LL", HpoEncodedValue.LOW),
			new SimpleEntry<>("LU", HpoEncodedValue.LOW),
			new SimpleEntry<>("MS", HpoEncodedValue.UNKNOWN),
			new SimpleEntry<>("N", HpoEncodedValue.NORMAL),
			new SimpleEntry<>("ND", HpoEncodedValue.NORMAL),
			new SimpleEntry<>("NEG", HpoEncodedValue.NORMAL),
			new SimpleEntry<>("NR", HpoEncodedValue.NORMAL),
			new SimpleEntry<>("NS", HpoEncodedValue.UNKNOWN),
			new SimpleEntry<>("null", HpoEncodedValue.UNKNOWN),
			new SimpleEntry<>("OBX", HpoEncodedValue.UNKNOWN),
			new SimpleEntry<>("POS", HpoEncodedValue.HIGH),
			new SimpleEntry<>("QCF", HpoEncodedValue.UNKNOWN),
			new SimpleEntry<>("R", HpoEncodedValue.UNKNOWN),
			new SimpleEntry<>("RR", HpoEncodedValue.HIGH),
			new SimpleEntry<>("S", HpoEncodedValue.UNKNOWN),
			new SimpleEntry<>("SDD", HpoEncodedValue.UNKNOWN),
			new SimpleEntry<>("SYN-R", HpoEncodedValue.UNKNOWN),
			new SimpleEntry<>("SYN-S", HpoEncodedValue.UNKNOWN),
			new SimpleEntry<>("TOX", HpoEncodedValue.HIGH),
			new SimpleEntry<>("U", HpoEncodedValue.HIGH),
			new SimpleEntry<>("VS", HpoEncodedValue.UNKNOWN),
			new SimpleEntry<>("W", HpoEncodedValue.ABNORMAL),
			new SimpleEntry<>("WR", HpoEncodedValue.HIGH))
            .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));
	
	// Known value strings that are interpretable
	private static Map<String,HpoEncodedValue> valueStrings = Collections.unmodifiableMap(Stream.of(
			new SimpleEntry<>("normal", HpoEncodedValue.NORMAL),
			new SimpleEntry<>("negative", HpoEncodedValue.NORMAL),
			new SimpleEntry<>("positive", HpoEncodedValue.HIGH),
			new SimpleEntry<>("reactive", HpoEncodedValue.HIGH))			
			.collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));
	
    // Map from the code system (e.g., http://hl7.org/fhir/v2/0078) to the external code for that system to the corresponding internal code
    private static Map<String, Map<String, HpoEncodedValue>> codelists = Collections.unmodifiableMap(Stream.of(            
    		new SimpleEntry<>("http://hl7.org/fhir/v2/0078", fhirv2interpretations),
    		new SimpleEntry<>("http://hl7.org/fhir/ValueSet/observation-interpretation", fhirv2interpretations),
    		new SimpleEntry<>(CODING_VALUE_STRING, valueStrings))
            .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));
    
    /**
     * Get the internal code that maps to the external coding.
     * @param coding the external coding
     * @return the internal code or null if either the system or code is not found
     */
    public static HpoEncodedValue getInternalCode(Coding coding) {
    	Map<String, HpoEncodedValue> systemMap = codelists.get(coding.getSystem());
    	
    	if (systemMap != null) {
    		return systemMap.get(coding.getCode());
    	}
    	
    	return null;
    }

}
