package ch.mobi.ufi.finance;

import ch.mobi.ufi.model.Contract;
import ch.mobi.ufi.model.Invoice;

public interface ChargingRepository {
	public boolean charge(Invoice invoice, Contract contract);
}
