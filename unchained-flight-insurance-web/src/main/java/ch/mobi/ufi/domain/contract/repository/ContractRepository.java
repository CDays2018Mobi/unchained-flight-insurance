package ch.mobi.ufi.domain.contract.repository;

import ch.mobi.ufi.domain.contract.entity.Contract;
import ch.mobi.ufi.domain.flight.vo.FlightIdentifier;

import java.time.Duration;
import java.util.List;

public interface ContractRepository {

    Contract createContract(FlightIdentifier flightIdentifier, Duration timeToCompensation);

    List<Contract> getContracts();
}
