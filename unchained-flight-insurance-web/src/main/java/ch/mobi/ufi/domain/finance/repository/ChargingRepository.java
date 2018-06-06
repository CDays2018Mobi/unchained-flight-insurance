package ch.mobi.ufi.domain.finance.repository;

import ch.mobi.ufi.domain.contract.entity.Contract;
import ch.mobi.ufi.domain.finance.entity.Invoice;

public interface ChargingRepository {
	public boolean charge(Invoice invoice, Contract contract);
}
