package ch.mobi.ufi.domain.contract.service;

import ch.mobi.ufi.domain.contract.entity.Contract;
import ch.mobi.ufi.domain.flight.entity.Flight;
import ch.mobi.ufi.domain.flight.repository.FlightRepository;
import ch.mobi.ufi.domain.flight.vo.FlightIdentifier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
public class ContractService {

    @NonNull
    private FlightRepository flightRepository;

    public Contract createContract(FlightIdentifier flightIdentifier, Duration timeToCompensation) {

        // find the flight expected arrival date (fresh)
        Flight flight = flightRepository.findFreshFlight(flightIdentifier);

        LOG.info("got flight {}: {}", flightIdentifier, flight);

        if (flight == null) {
            throw new IllegalArgumentException("could not find flight " + flightIdentifier);
        } else if (cannotInsure(flight)) {
            throw new IllegalArgumentException("flight cannot be insure :" + flightIdentifier + " (e.g. already arrived): " + flight);
        }

        // TODO certains vols sont déjà connus comme en avance ou en retard (expectedDate+effectiveDate+pas de status) => il faut tenir compte de l'effectiveDate s'elle  est connu

        LOG.info("creating contract for flight number {}: {}", flightIdentifier, timeToCompensation);
        return Contract.builder().flightIdentifier(flightIdentifier).timeToCompensation(timeToCompensation).build();
    }

    private boolean cannotInsure(Flight flight) {
        return false;
    }
}
