package ch.mobi.ufi.rest.endpoint;

import static org.springframework.http.ResponseEntity.ok;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.mobi.ufi.model.Flight;
import ch.mobi.ufi.supplier.FlightRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
public class FlightManagerRest {
	@Autowired
	private FlightRepository flightRepository;

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
}

