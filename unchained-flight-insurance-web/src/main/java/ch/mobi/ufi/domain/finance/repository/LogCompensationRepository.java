package ch.mobi.ufi.domain.finance.repository;

import ch.mobi.ufi.domain.contract.entity.Contract;
import ch.mobi.ufi.domain.flight.entity.Flight;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogCompensationRepository implements CompensationRepository {

	@Override
	public void createCompensation(Contract contract, Flight flight) {
		LOG.info("compensate for contract {} with delayed flight {}", contract, flight);
	}

}
