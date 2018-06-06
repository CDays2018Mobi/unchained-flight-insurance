package ch.mobi.ufi.domain.finance.repository;

import ch.mobi.ufi.domain.contract.entity.Contract;
import ch.mobi.ufi.domain.flight.entity.Flight;

public interface CompensationRepository {
	public void createCompensation(Contract contract, Flight flight);
}
