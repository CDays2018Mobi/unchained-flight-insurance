package ch.mobi.ufi.rest.endpoint;

import ch.mobi.ufi.domain.contract.service.ContractService;
import ch.mobi.ufi.domain.contract.vo.ContractDTO;
import ch.mobi.ufi.domain.contract.entity.Contract;
import ch.mobi.ufi.domain.flight.vo.FlightIdentifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@CrossOrigin
@RestController
public class ContractEndpoint {

    @Autowired
    private ContractService createContract;

    @RequestMapping(path = "/api/v1/contracts")
    @PostMapping
    public ResponseEntity<Contract> create(ContractDTO contractDTO) {
        LOG.info("create contract for: flightNumber={}, arrivalDate={}", contractDTO.getFlightNumber(), contractDTO.getArrivalDate());
        return ok(createContract.createContract(
                FlightIdentifier.builder()
                        .flightNumber(contractDTO.getFlightNumber())
                        .flightArrivalDate(contractDTO.getArrivalDate())
                        .build(),
                Duration.ofMinutes(10)));
    }
}

