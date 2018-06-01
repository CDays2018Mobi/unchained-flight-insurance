package ch.mobi.ufi.model;

import java.time.Duration;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Contract {
	private FlightIdentifier flightIdentifier;
	/**
	 * d�lai � attendre pour qu'il y ait une compensation
	 */
	private Duration timeToCompensation;
}
