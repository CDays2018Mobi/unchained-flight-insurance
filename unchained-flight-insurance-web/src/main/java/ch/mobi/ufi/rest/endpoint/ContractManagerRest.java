package ch.mobi.ufi.rest.endpoint;

import static org.springframework.http.ResponseEntity.ok;

import java.time.Duration;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.mobi.ufi.contract.ContractRepository;
import ch.mobi.ufi.model.Contract;
import ch.mobi.ufi.model.FlightIdentifier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
public class ContractManagerRest {
	// TODO injecter le ContractRepository
	@Autowired
	private ContractRepository contractRepository;

	/**
	 * test URL: http://localhost:9000/api/hello/v1/contracts/create?flightNumber=TP946&arrivalDate=2018-05-30
	 * @param flightNumber
	 * @param arrivalDate
	 * @return
	 */
	@RequestMapping(path = "/api/hello/v1/contracts/create")
    @GetMapping
    public ResponseEntity<Contract> create(
    		@RequestParam(value = "flightNumber", required = true) String flightNumber,
    		@RequestParam(value = "arrivalDate", required = true) 
	        @DateTimeFormat(iso = ISO.DATE) LocalDate arrivalDate) {
    	LOG.info("create contract for: flightNumber={}, arrivalDate={}", flightNumber, arrivalDate);
		Contract contract = contractRepository.createContract(FlightIdentifier.builder().flightNumber(flightNumber).flightArrivalDate(arrivalDate).build(), Duration.ofMinutes(10));
		return ok(contract);
    }
}

