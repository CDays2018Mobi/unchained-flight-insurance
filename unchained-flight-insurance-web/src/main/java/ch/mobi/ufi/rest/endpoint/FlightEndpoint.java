package ch.mobi.ufi.rest.endpoint;

import static org.springframework.http.ResponseEntity.ok;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.mobi.ufi.domain.flight.entity.Airline;
import ch.mobi.ufi.domain.flight.entity.Flight;
import ch.mobi.ufi.domain.flight.entity.InsurableFlight;
import ch.mobi.ufi.domain.flight.repository.FlightCache;
import ch.mobi.ufi.domain.flight.service.FlightService;
import ch.mobi.ufi.domain.price.PricingCalculator;
import ch.mobi.ufi.domain.risk.predictor.DelayEstimator;
import ch.mobi.ufi.domain.risk.predictor.RiskCoverage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/v1/flights")
public class FlightEndpoint {

    @Autowired
    private FlightService flightService;
    @Autowired
    private FlightCache flightCache;
    @Autowired
    private PricingCalculator pricingCalculator;
    @Autowired
    private DelayEstimator delayEstimator;
    
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

    /**
     * Returns the list of flights that can be insured.
     * @return
     */
    @GetMapping("/insurable")
    public ResponseEntity<List<InsurableFlight>> getInsurableFlights() {
    	int minDelay=60; // TODO use a configuration-based value from BayesianDelayEstimator.flightDelayThresholds
    	// TODO trouver un moyen de rafraichir le cache (workaround = polling manuel toutes les minutes sur /arrivals/refresh)
    	List<Flight> flights = flightCache.getFlights(flight -> flight.getExpectedArrivalDate().isAfter(LocalDateTime.now().minusHours(4)));
    	List<InsurableFlight> insurableFlights = flights.stream()
    			.map(f->InsurableFlight.builder()
    					.flight(f)
    					.delayProbability(delayEstimator.computeProbabilityOfBeingDelayed(f, minDelay))
    					.riskCoverages(pricingCalculator.getRiskCoverages(f, minDelay))
    					.build())
    			.collect(Collectors.toList());
        return ok(insurableFlights);
    }

}

