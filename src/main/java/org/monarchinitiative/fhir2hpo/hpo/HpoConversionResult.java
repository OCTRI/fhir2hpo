package org.monarchinitiative.fhir2hpo.hpo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.Observation;
import org.monarchinitiative.fhir2hpo.loinc.LoincId;

/**
 * This encapsulates the result of attempting to convert an observation to an HPO Term. This includes
 * the original observation and the results of each method tried.
 * 
 * @author yateam
 *
 */
public class HpoConversionResult {

	final Observation observation;
	final LoincId loincId;

	Exception exception; // An exception may be thrown before any methods have been tried

	// A map from the method description to the specific result for that method.
	Map<String, MethodConversionResult> methods = new HashMap<>();

	public HpoConversionResult(Observation observation, LoincId loincId) {
		this.observation = observation;
		this.loincId = loincId;
	}

	/**
	 * Get the original observation
	 * 
	 * @return
	 */
	public Observation getObservation() {
		return observation;
	}

	/**
	 * Get the relevant loincId for this result from the observation. Some observations may have more
	 * than one LoincId, yielding multiple conversion results.
	 * 
	 * @return the relevant loincId for this result or null if none exists.
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
