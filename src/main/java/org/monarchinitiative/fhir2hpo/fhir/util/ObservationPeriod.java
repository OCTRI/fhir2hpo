package org.monarchinitiative.fhir2hpo.fhir.util;

import java.util.Date;
import java.util.Optional;

/**
 * Represents the period of a FHIR observation. If a single effective date is given, it is used for both start and end.
 * @author yateam
 *
 */
public class ObservationPeriod {

	private Optional<Date> startDate = Optional.empty();
	private Optional<Date> endDate = Optional.empty();

	public Optional<Date> getStartDate() {
		return startDate;
	}

	public void setStartDate(Optional<Date> startDate) {
		this.startDate = startDate;
	}

	public Optional<Date> getEndDate() {
		return endDate;
	}

	public void setEndDate(Optional<Date> endDate) {
		this.endDate = endDate;
	}

}
