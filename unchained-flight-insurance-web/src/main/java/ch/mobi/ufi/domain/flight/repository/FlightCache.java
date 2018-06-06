package ch.mobi.ufi.domain.flight.repository;

import ch.mobi.ufi.domain.flight.entity.Flight;
import ch.mobi.ufi.domain.flight.parameters.DefaultFlightParameters;
import ch.mobi.ufi.domain.flight.service.FlightsSupplier;
import ch.mobi.ufi.domain.flight.vo.FlightIdentifier;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class FlightCache {

    private Map<FlightIdentifier, CachedFlight> cache = new HashMap<>(); // TODO migrate to a better cache mechanism, such as Cache2k

    /**
     * Returns the Flight for the given FlightIdentifier. The flight data is not older than 5 minutes.
     *
     * @param flightIdentifier the flight identifier
     * @return a flight or null if the flight is not found
     */
    // TODO is it meaningful to give the arrival date and not the departure date?
    public Flight findFreshFlight(FlightIdentifier flightIdentifier, FlightsSupplier supplier) {
        // look in cache for the flight corresponding to the flight number
        CachedFlight cachedFlight = cache.get(flightIdentifier);
        if (cachedFlight == null || cachedFlight.isOutdated()) {
            // not cached or outdated flight => refresh it
            update(supplier.getFlights(DefaultFlightParameters.builder().date(flightIdentifier.getFlightArrivalDate()).build()), flightIdentifier.getFlightArrivalDate());
            cachedFlight = cache.get(flightIdentifier);
        }
        return cachedFlight != null ? cachedFlight.getFlight() : null;
    }

    /**
     * Returns the list of cached flights, ordered by expected arrival date.
     *
     * @param filterFunction a function to filter the flights e.g. only the flights that are not yet arrived
     * @return
     */
    public List<Flight> getFlights(Predicate<Flight> filterFunction) {
        return cache.values().stream()
                .map(cachedFlight -> cachedFlight.flight)
                .filter(filterFunction)
                .sorted(Comparator.comparing(Flight::getExpectedArrivalDate))
                .collect(Collectors.toList());
    }

    public void update(List<Flight> flights, LocalDate currentDate) {
        flights.forEach(flight -> cache.put(
                buildKey(flight.getFlightNumber(), currentDate),
                CachedFlight.builder()
                        .flight(flight)
                        .obtainedDate(System.currentTimeMillis()).build()));
    }

    private FlightIdentifier buildKey(String flightNumber, LocalDate currentDate) {
        return FlightIdentifier.builder()
                .flightNumber(flightNumber)
                .flightArrivalDate(currentDate).build();
    }

    @Data
    @Builder
    private static class CachedFlight {
        private Flight flight;
        private long obtainedDate;

        public boolean isOutdated() {
            return System.currentTimeMillis() - obtainedDate > Duration.ofMinutes(5L).toMillis();
        }
    }

}
