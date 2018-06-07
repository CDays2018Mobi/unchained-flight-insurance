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

import ch.mobi.ufi.domain.flight.entity.Airline;
import ch.mobi.ufi.domain.flight.entity.Flight;
import ch.mobi.ufi.domain.flight.service.FlightService;
import ch.mobi.ufi.domain.flight.vo.FlightIdentifier;
import ch.mobi.ufi.domain.risk.predictor.DelayEstimator;
import ch.mobi.ufi.domain.price.PricingCalculator;
import ch.mobi.ufi.domain.risk.predictor.RiskCoverage;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/risk")
public class RiskEndpoint {

    @Autowired
    private FlightService flightService;
    @Autowired
    private PricingCalculator pricingCalculator;
    @Autowired
    private DelayEstimator delayEstimator;
    
    @GetMapping("/coverages")
    public ResponseEntity<List<RiskCoverage>> computeProbabilityOfBeingLate(
            @RequestParam(value = "flightNumber", required = true) String flightNumber,
            @RequestParam(value = "arrivalDate", required = true)
            @DateTimeFormat(iso = ISO.DATE) LocalDate arrivalDate) {
        Flight flight = flightService.findFreshFlight(FlightIdentifier.builder()
        		.flightNumber(flightNumber)
        		.flightArrivalDate(arrivalDate)
        		.build());
        return ok(pricingCalculator.getRiskCoverages(flight, 60));
    }

    /**
     * This service is only for debug purposes.
     * @param companyName
     * @param arrivalDate
     * @param minDelay
     * @return
     */
    @GetMapping("/delay-probability")
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
        return ok("probability of being late by at least " + selectedMinDelay + " minutes when taking " + companyName + 
        		" at " + arrivalDate + " : " + Math.floor(probabilityOfBeingLate * 100) + "%");
    }
}

