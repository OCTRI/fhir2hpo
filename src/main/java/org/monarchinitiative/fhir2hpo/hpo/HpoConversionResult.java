package org.monarchinitiative.fhir2hpo.hpo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.Observation;

/**
 * This encapsulates the result of attempting to convert an observation to an HPO Term. This includes
 * the original observation and the results of each method tried.
 * 
 * @author yateam
 *
 */
public class HpoConversionResult {
	
	final Observation observation;
	Exception exception; // An exception may be thrown before any methods have been tried
	// A map from the method description to the specific result for that method.
	Map<String, MethodConversionResult> methods = new HashMap<>();
	
	public HpoConversionResult(Observation observation) {
		this.observation = observation;
	}
	
	/**
	 * Get the original observation
	 * @return
	 */
	public Observation getObservation() {
		return observation;
	}
	
	/**
	 * Return whether the conversion resulted in a general exception not related to methods
	 * @return
	 */
	public Boolean hasException() {
		return exception != null;
	}
	
	/**
	 * Set a general exception on the conversion result
	 * @param exception
	 */
	public void setException(Exception exception) {
		this.exception = exception;
	}
	
	/**
	 * Get the general exception
	 * @return
	 */
	public Exception getException() {
		return exception;
	}
	
	/**
	 * Add a method conversion result
	 * @param result
	 */
	public void addMethodConversionResult(MethodConversionResult result) {
		methods.put(result.getMethod(), result);
	}

	/**
	 * Use to interrogate each method more closely
	 * @return all method results
	 */
	public Map<String, MethodConversionResult> getMethodResults() {
		return methods;
	}

	/**
	 * Return whether the conversion was able to identify any HpoTerms
	 * @return
	 */
	public Boolean hasSuccess() {
		return !hasException() && getHpoTerms().size() > 0;
	}
	
	/**
	 * @return All unique terms found in the observation. Multiple are possible if more than one method was successful. Empty
	 * 	if no method was successful
	 */
	public Set<HpoTermWithNegation> getHpoTerms() {
		return methods.values().stream().filter(MethodConversionResult::hasTerm).map(MethodConversionResult::getTerm).collect(Collectors.toSet());
	}

}
