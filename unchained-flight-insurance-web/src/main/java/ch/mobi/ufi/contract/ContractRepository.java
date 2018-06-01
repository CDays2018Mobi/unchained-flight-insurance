package ch.mobi.ufi.contract;

import java.time.Duration;
import java.util.List;

import ch.mobi.ufi.model.Contract;
import ch.mobi.ufi.model.FlightIdentifier;

public interface ContractRepository {

	Contract createContract(FlightIdentifier flightIdentifier, Duration timeToCompensation);
	List<Contract> getContracts();
}
