package ch.mobi.ufi.finance;

import ch.mobi.ufi.model.Contract;
import ch.mobi.ufi.model.Flight;

public interface CompensationRepository {
	public void createCompensation(Contract contract, Flight flight);
}
