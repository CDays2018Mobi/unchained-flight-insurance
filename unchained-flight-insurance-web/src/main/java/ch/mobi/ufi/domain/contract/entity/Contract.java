package ch.mobi.ufi.domain.contract.entity;

import java.time.Duration;

import ch.mobi.ufi.domain.flight.vo.FlightIdentifier;
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
