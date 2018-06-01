package ch.mobi.ufi.supplier;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ch.mobi.ufi.mapper.CsvMapper;
import ch.mobi.ufi.mapper.FlightCsvMapper;
import ch.mobi.ufi.model.Flight;
import ch.mobi.ufi.model.FlightIdentifier;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@Scope(value=ConfigurableBeanFactory.SCOPE_SINGLETON)
@Slf4j
public class FlightRepository {
	@Data
	@Builder
	private static class CachedFlight {
		private Flight flight;
		private long obtainedDate;
		public boolean isOutdated() {
			return System.currentTimeMillis()-obtainedDate>Duration.ofMinutes(5L).toMillis();
		}
	}
	
	private Map<FlightIdentifier, CachedFlight> cache = new HashMap<>(); // TODO migrate to a better cache mechanism, such as Cache2k
	
	private List<FlightsSupplier> suppliers = Arrays.asList(new GvaFlightsSupplier());
	
	
	public FlightRepository() {
		refreshFlightList();
	}

	/**
	 * Refreshes the list of flights by calling each supplier
	 * TODO schedule a daily call to all suppliers to refresh cache data
	 */
	private void refreshFlightList() {
		try (PrintWriter out = new PrintWriter("flights.csv")) {
			CsvMapper<Flight> csvMapper = new FlightCsvMapper();
			out.println(csvMapper.getCsvHeader());

			List<LocalDate> dates = new ArrayList<>();
			LocalDate currentDate = LocalDate.of(2018, Month.MAY, 21);
			for (int i=0; i<20; i++) {
				dates.add(currentDate);
				currentDate = currentDate.plusDays(1L);
			}

			for (FlightsSupplier flightsSupplier : suppliers) {

				// TODO find a way to initialize faster (to lower the application startup)
				dates.parallelStream()
					.map(arrivalDate->DefaultFlightParameters.builder().date(arrivalDate).build())
					.forEach(param->{
						List<Flight> flights = flightsSupplier.getFlights(param);
						flights.forEach(flight->out.println(csvMapper.toCsvRow(flight)));
						updateCache(flights, param.getDate());
					});

			}
		} catch (FileNotFoundException e) {
			LOG.error("could not store flight data", e);
		}
	}
	
	private void updateCache(List<Flight> flights, LocalDate currentDate) {
		flights.forEach(flight-> cache.put(
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
	
	/**
	 * Returns a flight supplier for the given flight identifier.
	 * @param flightIdentifier the flight identifier
	 * @return a non-null flight identifier
	 */
	public FlightsSupplier findSupplier(FlightIdentifier flightIdentifier) {
		return suppliers.get(0); // TODO find a smarter way to identify the correct supplier
	}
	
	/**
	 * Returns the Flight for the given FlightIndentifier. The flight data is not older than 5 minutes.
	 * @param flightIdentifier the flight identifier
	 * @return a flight or null if the flight is not found
	 */
	// TODO is it meaningful to give the arrival date and not the departure date?
	public Flight findFreshFlight(FlightIdentifier flightIdentifier) {
		// find the flight supplier for the flight corresponding to the flight number
		FlightsSupplier supplier = findSupplier(flightIdentifier);
		
		// look in cache for the flight corresponding to the flight number
		CachedFlight cachedFlight = cache.get(flightIdentifier);
		if (cachedFlight==null || cachedFlight.isOutdated()) {
			// not cached or outdated flight => refresh it
			updateCache(supplier.getFlights(DefaultFlightParameters.builder().date(flightIdentifier.getFlightArrivalDate()).build()), flightIdentifier.getFlightArrivalDate());
			cachedFlight = cache.get(flightIdentifier);
		}
		return cachedFlight!=null?cachedFlight.getFlight():null;
	}
	
	/**
	 * Returns the list of cached flights, ordered by expected arrival date.
	 * @param filterFunction a function to filter the flights e.g. only the flights that are not yet arrived
	 * @return
	 */
	public List<Flight> getFlights(Predicate<Flight> filterFunction) {
		return cache.values().stream()
				.map(cachedFlight->cachedFlight.flight)
				.filter(filterFunction)
				.sorted(Comparator.comparing(Flight::getExpectedArrivalDate))
				.collect(Collectors.toList());
	}

}
