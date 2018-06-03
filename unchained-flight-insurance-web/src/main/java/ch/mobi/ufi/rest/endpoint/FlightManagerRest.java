package ch.mobi.ufi.rest.endpoint;

import static org.springframework.http.ResponseEntity.ok;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.mobi.ufi.model.Airline;
import ch.mobi.ufi.model.Flight;
import ch.mobi.ufi.risk.DelayEstimator;
import ch.mobi.ufi.supplier.FlightRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
public class FlightManagerRest {
	@Autowired
	private FlightRepository flightRepository;
	@Autowired
	private DelayEstimator delayEstimator;

	/**
	 * test URL: http://localhost:9000/api/hello/v1/flight/flights
	 * @return
	 */
	@RequestMapping(path = "/api/hello/v1/flight/flights")
    @GetMapping
    public ResponseEntity<List<Flight>> getFlights() {
		return ok(flightRepository.getFlights(flight->flight.getExpectedArrivalDate()
				.isAfter(LocalDate.now().minusDays(1).atStartOfDay())));
    }
	
	/**
	 * test URL: http://localhost:9000/api/hello/v1/flight/refresh
	 * @return
	 */
	@RequestMapping(path = "/api/hello/v1/flight/refresh")
    @GetMapping
    public ResponseEntity<String> refreshFlights() {
		List<Flight> flights = flightRepository.refreshFlightList();
		return ok("refresh done: found "+flights.size()+" flights");
    }

	/**
	 * test URL: http://localhost:9000/api/hello/v1/flight/delay?companyName=EASYJET&arrivalDate=2018-06-03T13:00&minDelay=10
	 * @return
	 */
	@RequestMapping(path = "/api/hello/v1/flight/delay")
    @GetMapping
    public ResponseEntity<String> computeProbabilityOfBeingLate(
    		@RequestParam(value = "companyName", required = true) String companyName,
    		@RequestParam(value = "arrivalDate", required = true) 
	        @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime arrivalDate,
	        @RequestParam(value = "minDelay", required = false) Integer minDelay) {
		int selectedMinDelay = minDelay!=null?minDelay:60;
		double probabilityOfBeingLate = delayEstimator.computeProbabilityOfBeingDelayed(Flight.builder()
				.airline(Airline.builder().companyName(companyName).build())
				.expectedArrivalDate(arrivalDate) // sunday
				.build(), selectedMinDelay); // 32% of chance to be late by more than one hour
		return ok("probability of being late by at least "+selectedMinDelay+" minutes when taking "+companyName+" at "+arrivalDate+" : "+Math.floor(probabilityOfBeingLate*100)+"%");
    }
	
}

