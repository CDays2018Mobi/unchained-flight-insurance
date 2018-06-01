package ch.mobi.ufi.finance;

import ch.mobi.ufi.model.Contract;
import ch.mobi.ufi.model.Flight;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogCompensationRepository implements CompensationRepository {

	@Override
	public void createCompensation(Contract contract, Flight flight) {
		LOG.info("compensate for contract {} with delayed flight {}", contract, flight);
	}

}
