package ch.mobi.ufi.finance;

import ch.mobi.ufi.model.Contract;
import ch.mobi.ufi.model.Invoice;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogChargingRepository implements ChargingRepository {

	@Override
	public boolean charge(Invoice invoice, Contract contract) {
		LOG.info("charging {} for {}", invoice, contract);
		return true;
	}

}
