package ch.mobi.ufi.domain.flight.vo;

import java.time.LocalDate;

import ch.mobi.ufi.domain.flight.entity.Airline;
import lombok.Builder;
import lombok.Data;

/**
 * Wikipedia: A flight number, when combined with the name of the airline and the date, identifies a particular flight.
 * @author Julien
 *
 */
@Data
@Builder
public class FlightIdentifier {
	private String flightNumber;
	private Airline airline;
	private LocalDate flightArrivalDate;// TODO should we use the departure date?
}
