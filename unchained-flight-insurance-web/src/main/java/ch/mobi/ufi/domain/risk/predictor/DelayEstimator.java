package ch.mobi.ufi.domain.risk.predictor;

import java.util.List;

import ch.mobi.ufi.domain.flight.entity.Flight;

public interface DelayEstimator {

	/**
	 * Initializes the delay estimator.
	 * @param flights the list of flights used as training set
	 * @param flightDelayThresholds the list of flight delay thresholds (e.g. [60] will produce two categories: the flights that are delayed up to 60 minutes and the flights that are delayed by more than 60 minutes).
	 */
	void initialize(List<Flight> flights, List<Integer> flightDelayThresholds);

	/**
	 * Computes the probability that the given flight will be late by at least minDelay.
	 * @param flight the flight to check
	 * @param minDelay the minimum delay
	 * @return the probability that the flight is delayed by at least minDelay
	 */
	double computeProbabilityOfBeingDelayed(Flight flight, Integer minDelay);

}