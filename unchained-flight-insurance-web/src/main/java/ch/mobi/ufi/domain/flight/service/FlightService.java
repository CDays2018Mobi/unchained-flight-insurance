package ch.mobi.ufi.domain.flight.service;

import ch.mobi.ufi.domain.flight.entity.Flight;
import ch.mobi.ufi.domain.flight.parameters.DefaultFlightParameters;
import ch.mobi.ufi.domain.flight.repository.FlightCache;
import ch.mobi.ufi.domain.flight.util.FlightCsvMapper;
import ch.mobi.ufi.domain.flight.vo.FlightIdentifier;
import ch.mobi.ufi.domain.risk.predictor.DelayEstimator;
import ch.mobi.ufi.domain.util.CsvMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class FlightService {

    static final LocalDate FIRST_DAY_OF_ARRIVAL_LOG = LocalDate.of(2018, Month.MAY, 21);

    static final int FLIGHT_COMPENSABLE_DELAY_THRESHOLD = 60;

    @NonNull
    private DelayEstimator delayEstimator;
    @NonNull
    private FlightCache flightCache;

    private List<FlightsSupplier> suppliers = Collections.singletonList(new GvaFlightsSupplier());

    /**
     * Returns the Flight for the given FlightIdentifier. The flight data is not older than 5 minutes.
     *
     * @param flightIdentifier the flight identifier
     * @return a flight or null if the flight is not found
     */
    // TODO is it meaningful to give the arrival date and not the departure date?
    public Flight findFreshFlight(FlightIdentifier flightIdentifier) {
        // find the flight supplier for the flight corresponding to the flight number
        FlightsSupplier supplier = findSupplier(flightIdentifier);
        return flightCache.findFreshFlight(flightIdentifier, supplier);
    }

    /**
     * Returns a flight supplier for the given flight identifier.
     *
     * @param flightIdentifier the flight identifier
     * @return a non-null flight identifier
     */
    public FlightsSupplier findSupplier(FlightIdentifier flightIdentifier) {
        return suppliers.get(0); // TODO find a smarter way to identify the correct supplier
    }

    @PostConstruct
    public void postConstruct() {
        // refreshFlightList();
    }

    /**
     * Refreshes the list of flights by calling each supplier
     * TODO schedule a daily call to all suppliers to refresh cache data
     */
    public List<Flight> refreshFlightList() {
        List<LocalDate> dates = getAllArrivalDays(FIRST_DAY_OF_ARRIVAL_LOG);
        List<Flight> allFlights = provisionFlights(dates);
        initializeDelayEstimator(allFlights, FLIGHT_COMPENSABLE_DELAY_THRESHOLD);
        storeFlightsToCSV(allFlights);
        return allFlights;
    }

    private List<LocalDate> getAllArrivalDays(LocalDate currentDate) {
        List<LocalDate> arrivalDates = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            arrivalDates.add(currentDate);
            currentDate = currentDate.plusDays(1L);
        }
        return arrivalDates;
    }

    private List<Flight> provisionFlights(List<LocalDate> dates) {
        List<Flight> allFlights = new ArrayList<>();
        for (FlightsSupplier flightsSupplier : suppliers) {
            // TODO find a way to initialize faster (to lower the application startup)
            dates.parallelStream()
                    .map(arrivalDate -> DefaultFlightParameters.builder().date(arrivalDate).build())
                    .forEach(param -> {
                        List<Flight> flights = flightsSupplier.getFlights(param);
                        allFlights.addAll(flights);
                        flightCache.update(flights, param.getDate());
                    });
        }
        allFlights.sort(Comparator.comparing(Flight::getExpectedArrivalDate));
        return allFlights;
    }

    private void initializeDelayEstimator(List<Flight> flights, int delayThreshold) {
        delayEstimator.initialize(flights, Arrays.asList(10, delayThreshold));
//		delayEstimator.computeProbabilityOfBeingDelayed(Flight.builder()
//				.airline(Airline.builder().companyName("EASYJET").build())
//				.expectedArrivalDate(toDate("2018-06-03 13:00")) // sunday
//				.build(), delayThreshold); // 32% of chance to be late by more than one hour
//		delayEstimator.computeProbabilityOfBeingDelayed(Flight.builder()
//				.airline(Airline.builder().companyName("EASYJET").build())
//				.expectedArrivalDate(toDate("2018-06-03 07:00")) // sunday
//				.build(), delayThreshold); // 7% of chance to be late by more than one hour
//		delayEstimator.computeProbabilityOfBeingDelayed(Flight.builder()
//				.airline(Airline.builder().companyName("QATAR AIRWAYS").build())
//				.expectedArrivalDate(toDate("2018-06-03 13:00")) // sunday
//				.build(), delayThreshold); // 0% of chance to be late by more than one hour
    }

    private void storeFlightsToCSV(List<Flight> flights) {
        // store the flights to CSV
        try (PrintWriter out = new PrintWriter("flights.csv")) {
            CsvMapper<Flight> csvMapper = new FlightCsvMapper();
            out.println(csvMapper.getCsvHeader());
            flights.forEach(flight -> out.println(csvMapper.toCsvRow(flight)));
        } catch (FileNotFoundException e) {
            LOG.error("could not store flight data", e);
        }
    }
}
