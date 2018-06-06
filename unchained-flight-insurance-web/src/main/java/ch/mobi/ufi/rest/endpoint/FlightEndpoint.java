package ch.mobi.ufi.rest.endpoint;

import ch.mobi.ufi.domain.flight.entity.Flight;
import ch.mobi.ufi.domain.flight.repository.FlightCache;
import ch.mobi.ufi.domain.flight.service.FlightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/v1/flights")
public class FlightEndpoint {

    @Autowired
    private FlightService flightService;
    @Autowired
    private FlightCache flightCache;

    @GetMapping("/arrivals/search")
    public ResponseEntity<List<Flight>> getFlights() {
        return ok(flightCache.getFlights(flight -> flight.getExpectedArrivalDate()
                .isAfter(LocalDate.now().minusDays(1).atStartOfDay())));
    }

    @GetMapping("/arrivals/refresh") // not compliant but convenient to refresh from the browser
    @PutMapping("/arrivals/refresh")
    public ResponseEntity<String> refreshFlights() {
        List<Flight> flights = flightService.refreshFlightList();
        return ok("refresh done: found " + flights.size() + " flights");
    }

}

