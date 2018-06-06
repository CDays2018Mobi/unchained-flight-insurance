package ch.mobi.ufi.domain.contract.repository;

import ch.mobi.ufi.domain.contract.entity.Contract;
import ch.mobi.ufi.domain.flight.repository.FlightCache;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class InMemoryContractRepository implements ContractRepository {

    @NonNull
    private FlightCache flightRepository;

    @Getter
    private List<Contract> contracts = new ArrayList<>();

    public void putContract(Contract contract) {
        contracts.add(contract);
    }

}