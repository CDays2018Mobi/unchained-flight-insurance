package ch.mobi.ufi.rest.config;

import ch.mobi.ufi.document.DefaultDocumentGenerator;
import ch.mobi.ufi.mailing.Notifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.mobi.ufi.domain.contract.repository.ContractRepository;
import ch.mobi.ufi.domain.contract.repository.InMemoryContractRepository;
import ch.mobi.ufi.domain.contract.service.ContractService;
import ch.mobi.ufi.domain.finance.repository.ChargingRepository;
import ch.mobi.ufi.domain.finance.repository.CompensationRepository;
import ch.mobi.ufi.domain.finance.repository.LogChargingRepository;
import ch.mobi.ufi.domain.finance.repository.LogCompensationRepository;
import ch.mobi.ufi.domain.flight.repository.FlightCache;
import ch.mobi.ufi.domain.flight.service.FlightService;
import ch.mobi.ufi.domain.risk.predictor.BayesianDelayEstimator;
import ch.mobi.ufi.domain.risk.predictor.DelayEstimator;
import ch.mobi.ufi.domain.price.PricingCalculator;
import lombok.NonNull;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public CompensationRepository compensationRepository() {
        return new LogCompensationRepository();
    }

    @Bean
    public ChargingRepository chargingRepository() {
        return new LogChargingRepository();
    }

    @Bean
    public DelayEstimator delayEstimator() {
        return new BayesianDelayEstimator();
    }

    @Bean
    public PricingCalculator pricingCalculator(DelayEstimator delayEstimator) {
        return new PricingCalculator(delayEstimator);
    }
    
    @Bean
    public FlightCache flightCache() {
        return new FlightCache();
    }

    @Bean
    public FlightService flightService(DelayEstimator delayEstimator, FlightCache flightCache) {
        return new FlightService(delayEstimator, flightCache);
    }

    @Bean
    public ContractRepository contractRepository(FlightCache flightCache) {
        return new InMemoryContractRepository(flightCache);
    }

    @Bean
    public ContractService contractService(
            ContractRepository contractRepository,
            FlightService flightService,
            CompensationRepository compensationRepository,
            ChargingRepository chargingRepository,
            DefaultDocumentGenerator documentGenerator,
            Notifier notifier) {
        return new ContractService(
                contractRepository,
                flightService,
                compensationRepository,
                chargingRepository,
                documentGenerator,
                notifier);
    }
}
