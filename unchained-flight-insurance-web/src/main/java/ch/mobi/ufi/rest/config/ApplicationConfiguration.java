package ch.mobi.ufi.rest.config;

import ch.mobi.ufi.domain.contract.repository.ContractRepository;
import ch.mobi.ufi.domain.contract.repository.ContractRepositoryImpl;
import ch.mobi.ufi.domain.contract.service.ContractService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public ContractRepository contractRepository() {
        return new ContractRepositoryImpl();
    }

    @Bean
    public ContractService contractService() {
        return new ContractService();
    }

}
