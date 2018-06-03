package ch.mobi.ufi.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
	
	/**
	 * Returns the effective delay in minutes from the expected arrival date to the effective arrival date.
	 * Returns null when some data is missing
	 * @return 
	 */
	public Long getEffectiveDelay() {
		return getEffectiveArrivalDate()!=null?
				ChronoUnit.MINUTES.between(getExpectedArrivalDate(), getEffectiveArrivalDate()):null;
	}
}
