package ch.mobi.ufi.rest.endpoint;

import static org.springframework.http.ResponseEntity.ok;

import java.time.LocalDate;
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

import ch.mobi.ufi.domain.flight.entity.Flight;
import ch.mobi.ufi.domain.flight.service.FlightService;
import ch.mobi.ufi.domain.flight.vo.FlightIdentifier;
import ch.mobi.ufi.domain.risk.predictor.PricingCalculator;
import ch.mobi.ufi.domain.risk.predictor.RiskCoverage;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/risk")
public class RiskEndpoint {

    @Autowired
    private FlightService flightService;
    @Autowired
    private PricingCalculator pricingCalculator;

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

}

