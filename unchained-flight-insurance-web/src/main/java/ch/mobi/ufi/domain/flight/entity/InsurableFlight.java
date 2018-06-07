package ch.mobi.ufi.domain.flight.entity;

import java.util.List;

import ch.mobi.ufi.domain.risk.predictor.RiskCoverage;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InsurableFlight {
	private Flight flight;
	private double delayProbability;
	private List<RiskCoverage> riskCoverages;
}
