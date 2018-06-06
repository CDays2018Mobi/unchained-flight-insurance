package ch.mobi.ufi.domain.contract.repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ch.mobi.ufi.domain.finance.repository.ChargingRepository;
import ch.mobi.ufi.domain.finance.repository.CompensationRepository;
import ch.mobi.ufi.domain.finance.repository.LogChargingRepository;
import ch.mobi.ufi.domain.finance.repository.LogCompensationRepository;
import ch.mobi.ufi.domain.contract.entity.Contract;
import ch.mobi.ufi.domain.flight.entity.Flight;
import ch.mobi.ufi.domain.flight.vo.FlightIdentifier;
import ch.mobi.ufi.domain.finance.entity.Invoice;
import ch.mobi.ufi.domain.flight.repository.FlightRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
/*
 * Workflow:
 * - je planifie l'assurance d'un vol et je regarde le tarif
 * - je confirme le contrat
 * - je suis débité sur ma carte de crédit
 * - si mon vol a du retard, je reçois une prestation
 */
@Component
@Scope(value=ConfigurableBeanFactory.SCOPE_SINGLETON)
@Slf4j
@Getter
public class ContractRepositoryImpl implements ContractRepository {

	 private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);// TODO make scheduling persistent
	 
	 @Autowired
	 private FlightRepository flightRepository;
	 // TODO inject the CompensationManager
	 private CompensationRepository compensationRepository = new LogCompensationRepository();
	 // TODO inject the ChargingRepository
	 private ChargingRepository chargingRepository = new LogChargingRepository();
	 
	 private List<Contract> contracts = new ArrayList<>();

	 /**
	 * Planifie l'appel du supplier ad�quat en fonction du contrat.
	 * @param contract
	 */
	@Override
	public Contract createContract(FlightIdentifier flightIdentifier, Duration timeToCompensation) { // TODO add "customer" and "compensationAmount" param
		// find the flight expected arrival date (fresh)
		Flight flight = flightRepository.findFreshFlight(flightIdentifier);
		LOG.info("got flight {}: {}", flightIdentifier, flight);
		if (flight==null) {
			// no flight found => raise exception
			throw new IllegalArgumentException("could not find flight "+flightIdentifier);
		} else if (cannotInsure(flight)) {
			// flight cannot be insured (e.g. is already arrived) => raise exception
			throw new IllegalArgumentException("flight cannot be insure :"+flightIdentifier+" (e.g. already arrived): "+flight);
		}
		// TODO certains vols sont déjà connus comme en avance ou en retard (expectedDate+effectiveDate+pas de status) => il faut tenir compte de l'effectiveDate s'elle  est connu

		// create the contract
		LOG.info("creating contract for flight number {}: {}", flightIdentifier, timeToCompensation);
		Contract contract = Contract.builder().flightIdentifier(flightIdentifier).timeToCompensation(timeToCompensation).build();

		// send the invoice
		boolean chargingSuccess = chargingRepository.charge(Invoice.builder().build(), contract);
		if (!chargingSuccess) {
			LOG.error("failed to charge the contract, so I will not process it");
			return null;
		}
		
		// store contract in repo
		contracts.add(contract);

		// TODO manage the transaction (store contract + charge the invoice)
		
		long delay=5;
		class RefreshFlightStatusRunnable implements Runnable {
			@Override
			public void run() {
				// get a fresh flight data from the FlightSupplierManager
				Flight freshFlight = flightRepository.findFreshFlight(flightIdentifier);
				LOG.info("got fresh flight {}: {}", flightIdentifier, freshFlight);
				
				// if the flight is later than the contract time to compensation, pay the compensation
				LocalDateTime effectiveArrivalDate = freshFlight.getEffectiveArrivalDate();
				if (freshFlight.getFlightStatus()==null && 
					effectiveArrivalDate!=null && 
					effectiveArrivalDate.isAfter(freshFlight.getExpectedArrivalDate().plus(timeToCompensation))) {
					// flight is arrived but is late => pay the compensation
					compensationRepository.createCompensation(contract, flight);
				} else if (effectiveArrivalDate==null || freshFlight.getFlightStatus()==null) {
					// flight is not yet arrived => schedule another for later
					LOG.info("rescheduling flight refresh for {}", flight);
					scheduler.schedule(this, delay, TimeUnit.MINUTES);
				} else {
					// flight is on time => no compensation to pay
					LOG.info("flight {} arrived within the {} time to compensation => no compensation to pay", freshFlight, timeToCompensation);
				}
			}
		}
		LocalDateTime firstRefreshDateTime = flight.getExpectedArrivalDate().plus(timeToCompensation);
		long initialDelayMs = ChronoUnit.MILLIS.between(LocalDateTime.now(), firstRefreshDateTime);
		LOG.info("scheduling flight refresh at {} ({} minutes) for {}", firstRefreshDateTime, Duration.ofMillis(initialDelayMs).toMinutes(), flight);
		scheduler.schedule(new RefreshFlightStatusRunnable(), initialDelayMs, TimeUnit.MILLISECONDS);
		return contract;
	}

	private boolean cannotInsure(Flight flight) {
		return flight.getFlightStatus()!=null;
		// TODO potentially check effective/expected arrival date ; flight.getEffectiveArrivalDate()!=null;
	}

//		contractRepository.createContract(FlightIdentifier.builder()
//				.flightNumber("LX1473")
//				.flightArrivalDate(LocalDate.of(2018, Month.MAY, 30)).build(), Duration.ofMinutes(10L));
//		contractRepository.createContract(FlightIdentifier.builder()
//				.flightNumber("AZ568")
//				.flightArrivalDate(LocalDate.of(2018, Month.MAY, 30)).build(), Duration.ofMinutes(10L));
//		contractRepository.createContract(FlightIdentifier.builder()
//				.flightNumber("LH2390")
//				.flightArrivalDate(LocalDate.of(2018, Month.MAY, 30)).build(), Duration.ofMinutes(10L));
//		contractRepository.createContract(FlightIdentifier.builder()
//				.flightNumber("LX2818")
//				.flightArrivalDate(LocalDate.of(2018, Month.MAY, 30)).build(), Duration.ofMinutes(10L));
//		contractRepository.createContract(FlightIdentifier.builder()
//				.flightNumber("TP946")
//				.flightArrivalDate(LocalDate.of(2018, Month.MAY, 30)).build(), Duration.ofMinutes(10L));
}
