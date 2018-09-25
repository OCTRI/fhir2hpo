package org.monarchinitiative.fhir2hpo.hpo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.monarchinitiative.fhir2hpo.fhir.util.ObservationLoincInfo;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;

/**
 * This encapsulates the result of attempting to convert a LOINC to an HPO Term. This includes
 * the observation information specific to the LOINC and the results of each method tried. 
 * 
 * @author yateam
 *
 */
public class LoincConversionResult {

	private final LoincId loincId;
	private ObservationLoincInfo observationLoincInfo;
	
	// An exception may be thrown before any methods have been tried
	Exception exception; 

	// A map from the method description to the specific result for that method.
	Map<String, MethodConversionResult> methods = new HashMap<>();

	public LoincConversionResult(LoincId loincId) {
		this.loincId = loincId;
	}
	
	/**
	 * Set the observation information specific to the LOINC.
	 * @param observationLoincInfo
	 */
	public void setObservationLoincInfo(ObservationLoincInfo observationLoincInfo) {
		this.observationLoincInfo = observationLoincInfo;
	}

	/**
	 * Get the observation information specific to the LOINC
	 * 
	 * @return the relevant observation information for the LOINC. This may be null if an exception is thrown
	 * before it can be set.
	 */
	public ObservationLoincInfo getObservationLoincInfo() {
		return observationLoincInfo;
	}

	/**
	 * Get the relevant loincId for this result. Some observations may have more than one LoincId, yielding 
	 * multiple conversion results.
	 * 
	 * @return the relevant loincId for this result
	 */
	public LoincId getLoincId() {
		return loincId;
	}

	/**
	 * Return whether the conversion resulted in a general exception not related to methods.
	 * 
	 * @return true if a general exception was thrown, false otherwise
	 */
	public Boolean hasException() {
		return exception != null;
	}

	/**
	 * Set a general exception on the conversion result.
	 * 
	 * @param exception
	 */
	public void setException(Exception exception) {
		this.exception = exception;
	}

	/**
	 * Get the general exception.
	 * 
	 * @return the general exception thrown for this conversion
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * Add a method conversion result.
	 * 
	 * @param result
	 */
	public void addMethodConversionResult(MethodConversionResult result) {
		methods.put(result.getMethod(), result);
	}

	/**
	 * Use to interrogate each method more closely.
	 * 
	 * @return all method results
	 */
	public Map<String, MethodConversionResult> getMethodResults() {
		return methods;
	}

	/**
	 * Return whether the conversion was able to identify any HpoTerms.
	 * 
	 * @return
	 */
	public Boolean hasSuccess() {
		return !hasException() && getHpoTerms().size() > 0;
	}

	/**
	 * Get the HpoTerms successfully converted for the LoincId and Observation. Multiple are possible if more than one
	 * method was successful.
	 * 
	 * @return All unique terms for this LoincId and observation. Empty if no method was successful.
	 */
	public Set<HpoTermWithNegation> getHpoTerms() {
		return methods.values().stream().filter(MethodConversionResult::hasTerm).map(MethodConversionResult::getTerm)
				.collect(Collectors.toSet());
	}

}
