package ch.mobi.ufi.domain.finance.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;

import ch.mobi.ufi.domain.contract.entity.Contract;
import ch.mobi.ufi.domain.finance.entity.Invoice;
import ch.mobi.ufi.domain.flight.vo.FlightIdentifier;
import ch.mobi.ufi.domain.price.PricingCalculator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogChargingRepository implements ChargingRepository {
    @Autowired
    private PricingCalculator pricingCalculator;
	private Map<FlightIdentifier, AtomicInteger> contractCounts = new HashMap<>();

	@Override
	public boolean charge(Invoice invoice, Contract contract) {
		LOG.info("charging {} for {}", invoice, contract);
		AtomicInteger contractCount = contractCounts.computeIfAbsent(contract.getFlightIdentifier(), k->new AtomicInteger());
		pricingCalculator.updateParameters(contract.getFlightIdentifier(), contractCount.incrementAndGet());
		return true;
	}

	@Override
	public int getContractCount(FlightIdentifier flightIdentifier) {
		return contractCounts.getOrDefault(flightIdentifier, new AtomicInteger(0)).get();
	}

}
