package org.monarchinitiative.fhir2hpo.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.fhir2hpo.loinc.Loinc2HpoAnnotation;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;
import org.monarchinitiative.fhir2hpo.loinc.LoincScale;
import org.monarchinitiative.fhir2hpo.loinc.NonInterpretableLoincAnnotation;
import org.monarchinitiative.fhir2hpo.loinc.exception.LoincException;

public class NonInterpretableLoincParser {

	private static final Logger logger = LogManager.getLogger();

	private static final int COL_LOINC_ID = 0;
	private static final int COL_LOINC_SCALE = 1;
	private static final int COL_REASON = 2;
	private static final int NUM_COL = 3;

	public static Map<LoincId, Loinc2HpoAnnotation> parse(InputStream stream)
			throws FileNotFoundException {
		Map<LoincId, Loinc2HpoAnnotation> annotations = new LinkedHashMap<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		reader.lines().forEach(serialized -> {
			String[] elements = serialized.split("\\t");
			if (elements.length == NUM_COL && !serialized.startsWith("loincId")) {
				try {
					LoincId loincId = new LoincId(elements[COL_LOINC_ID]);
					LoincScale loincScale = LoincScale.string2enum(elements[COL_LOINC_SCALE]);
					String reason = elements[COL_REASON];
					annotations.put(loincId, new NonInterpretableLoincAnnotation(loincId, loincScale, reason));
				} catch (LoincException e) {
					logger.error("Malformed loinc code line: " + serialized);
				}
			} else {
				if (elements.length != NUM_COL) {
					logger.error(
							String.format("line does not have " + NUM_COL + " elements, but has %d elements. Line: %s",
									elements.length, serialized));
				} else {
					logger.debug("line is header: " + serialized);
				}

			}
		});
		
		return annotations;
	}
}
