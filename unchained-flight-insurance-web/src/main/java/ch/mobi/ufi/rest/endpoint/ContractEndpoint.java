package ch.mobi.ufi.rest.endpoint;

import ch.mobi.ufi.domain.contract.entity.Contract;
import ch.mobi.ufi.domain.contract.service.ContractService;
import ch.mobi.ufi.domain.contract.vo.ContractDTO;
import ch.mobi.ufi.domain.flight.vo.FlightIdentifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Duration;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/v1/contracts")
public class ContractEndpoint {

    @Autowired
    private ContractService contractService;

    @PostMapping
    public ResponseEntity<Contract> create(@Valid @RequestBody ContractDTO contractDTO) {
        LOG.info("create contract for: flightId={}, arrivalDate={}", contractDTO.getFlightId(), contractDTO.getArrivalDate());
        return ok(contractService.createContract(
                FlightIdentifier.builder()
                        .flightNumber(contractDTO.getFlightId())
                        .flightArrivalDate(contractDTO.getArrivalDate())
                        .build(),
                Duration.ofMinutes(10)));
    }
}

