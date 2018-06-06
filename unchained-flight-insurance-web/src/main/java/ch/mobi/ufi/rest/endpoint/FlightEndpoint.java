package ch.mobi.ufi.rest.endpoint;

import ch.mobi.ufi.domain.flight.entity.Airline;
import ch.mobi.ufi.domain.flight.entity.Flight;
import ch.mobi.ufi.domain.flight.repository.FlightCache;
import ch.mobi.ufi.domain.flight.service.FlightService;
import ch.mobi.ufi.domain.risk.predictor.DelayEstimator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/v1/flight")
public class FlightEndpoint {

    @Autowired
    private FlightService flightService;
    @Autowired
    private FlightCache flightCache;
    @Autowired
    private DelayEstimator delayEstimator;

    @GetMapping("/flights")
    public ResponseEntity<List<Flight>> getFlights() {
        return ok(flightCache.getFlights(flight -> flight.getExpectedArrivalDate()
                .isAfter(LocalDate.now().minusDays(1).atStartOfDay())));
    }

    @PutMapping("/refresh")
    public ResponseEntity<String> refreshFlights() {
        List<Flight> flights = flightService.refreshFlightList();
        return ok("refresh done: found " + flights.size() + " flights");
    }

    @GetMapping("/delay")
    public ResponseEntity<String> computeProbabilityOfBeingLate(
            @RequestParam(value = "companyName", required = true) String companyName,
            @RequestParam(value = "arrivalDate", required = true)
            @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime arrivalDate,
            @RequestParam(value = "minDelay", required = false) Integer minDelay) {
        int selectedMinDelay = minDelay != null ? minDelay : 60;
        double probabilityOfBeingLate = delayEstimator.computeProbabilityOfBeingDelayed(Flight.builder()
                .airline(Airline.builder().companyName(companyName).build())
                .expectedArrivalDate(arrivalDate) // sunday
                .build(), selectedMinDelay); // 32% of chance to be late by more than one hour
        return ok("probability of being late by at least " + selectedMinDelay + " minutes when taking " + companyName + " at " + arrivalDate + " : " + Math.floor(probabilityOfBeingLate * 100) + "%");
    }

}

