package ch.mobi.ufi.domain.finance.repository;

import ch.mobi.ufi.domain.contract.entity.Contract;
import ch.mobi.ufi.domain.finance.entity.Invoice;
import ch.mobi.ufi.domain.flight.vo.FlightIdentifier;

public interface ChargingRepository {
	public boolean charge(Invoice invoice, Contract contract);
	
	/**
	 * Returns the number of contracts that were charged for the given flight.
	 * @param flightIdentifier the identification of a flight
	 * @return 0 if no contract has been on the flight
	 */
	public int getContractCount(FlightIdentifier flightIdentifier);
}
