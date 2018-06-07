package ch.mobi.ufi.domain.flight.entity;

import java.util.List;

import ch.mobi.ufi.domain.risk.predictor.RiskCoverage;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InsurableFlight {
	private Flight flight;
	/**
	 * Probability that the flight arrive late (late=e.g. more than one hour delay).
	 * Varies from 0 (0%) to 1 (100%).
	 */
	private double delayProbability;
	private List<RiskCoverage> riskCoverages;
}
