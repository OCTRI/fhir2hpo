package org.monarchinitiative.fhir2hpo.loinc;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.hl7.fhir.dstu3.model.Observation;
import org.monarchinitiative.fhir2hpo.hpo.HpoTermWithNegation;
import org.monarchinitiative.fhir2hpo.hpo.LoincConversionResult;
import org.monarchinitiative.fhir2hpo.loinc.exception.NonInterpretableLoincException;

/**
 * This annotation pertains to LOINCs that can not or should not be converted to HPO. This allows distinction between
 * LOINCs that can be annotated but have not been yet and LOINCs where a conversion does not make sense. A reason can
 * optionally be provided.
 * 
 * @author yateam
 *
 */
public class NonInterpretableLoincAnnotation implements Loinc2HpoAnnotation {

	private final LoincId loincId;
	private final LoincScale loincScale;
	private final String reason;

	public NonInterpretableLoincAnnotation(LoincId loincId, LoincScale loincScale, String reason) {
		this.loincId = loincId;
		this.loincScale = loincScale;
		// Provide a default reason if one doesn't exist
		this.reason = StringUtils.isBlank(reason)?"Not Interpretable":reason;
	}

	@Override
	public LoincId getLoincId() {
		return loincId;
	}

	@Override
	public LoincScale getLoincScale() {
		return loincScale;
	}

	@Override
	public Collection<HpoTermWithNegation> getHpoTerms() {
		return new ArrayList<>();
	}

	@Override
	public LoincConversionResult convert(Observation observation) {
		LoincConversionResult result = new LoincConversionResult(null);
		result.setException(new NonInterpretableLoincException(reason));
		return result;
	}

}
