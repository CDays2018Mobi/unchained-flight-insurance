package ch.mobi.ufi.model;

import java.time.Duration;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Flight {
	private LocalDateTime expectedArrivalDate;
	/**
	 * When the flight will be for sure late but is not arrived yet, this date could be provided but without a flight status.
	 */
	private LocalDateTime effectiveArrivalDate;
	private String startingAirport;
	private Airline airline;
	private String flightNumber;
	private FlightStatus flightStatus;
	private Duration expectedDelay;	
}
