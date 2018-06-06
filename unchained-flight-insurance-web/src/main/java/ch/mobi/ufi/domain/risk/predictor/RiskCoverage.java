package ch.mobi.ufi.domain.risk.predictor;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RiskCoverage {
	private String name;
	/**
	 * Amount in CHF that the insured person will receive when the flight is delayed.
	 */
	private double insuredAmount;
	
	/**
	 * Amount in CHF that the insured person will pay to be insured.
	 */
	private double premiumAmount;
	
	/**
	 * false when the coverage is not available (e.g. cost is too high or flight is already arrived)
	 */
	private boolean available;
}
