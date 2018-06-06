package ch.mobi.ufi.domain.flight.service;

import java.util.List;

import ch.mobi.ufi.domain.flight.entity.Flight;
import ch.mobi.ufi.domain.flight.parameters.FlightParameters;

@FunctionalInterface
public interface FlightsSupplier {
	public List<Flight> getFlights(FlightParameters flightParameters);
}
