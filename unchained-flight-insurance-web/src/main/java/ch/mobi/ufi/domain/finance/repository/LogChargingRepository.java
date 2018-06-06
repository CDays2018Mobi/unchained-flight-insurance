package ch.mobi.ufi.domain.finance.repository;

import ch.mobi.ufi.domain.contract.entity.Contract;
import ch.mobi.ufi.domain.finance.entity.Invoice;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogChargingRepository implements ChargingRepository {

	@Override
	public boolean charge(Invoice invoice, Contract contract) {
		LOG.info("charging {} for {}", invoice, contract);
		return true;
	}

}
