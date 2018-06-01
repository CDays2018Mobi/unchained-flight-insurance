package ch.mobi.ufi.supplier;

import java.util.List;

import ch.mobi.ufi.model.Flight;

@FunctionalInterface
public interface FlightsSupplier {
	public List<Flight> getFlights(FlightParameters flightParameters);
}
