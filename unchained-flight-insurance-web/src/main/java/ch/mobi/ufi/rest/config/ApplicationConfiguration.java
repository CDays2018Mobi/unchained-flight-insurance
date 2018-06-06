package ch.mobi.ufi.rest.config;

import ch.mobi.ufi.domain.contract.service.ContractService;
import ch.mobi.ufi.domain.flight.repository.FlightRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public FlightRepository flightRepository() {
        return new FlightRepository();
    }

    @Bean
    public ContractService contractService() {
        return new ContractService(flightRepository());
    }
}
