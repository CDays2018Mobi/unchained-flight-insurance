package ch.mobi.ufi.domain.contract.service;

import ch.mobi.ufi.document.DefaultDocumentGenerator;
import ch.mobi.ufi.domain.contract.entity.Contract;
import ch.mobi.ufi.domain.contract.repository.ContractRepository;
import ch.mobi.ufi.domain.finance.entity.Invoice;
import ch.mobi.ufi.domain.finance.repository.ChargingRepository;
import ch.mobi.ufi.domain.finance.repository.CompensationRepository;
import ch.mobi.ufi.domain.flight.entity.Flight;
import ch.mobi.ufi.domain.flight.service.FlightService;
import ch.mobi.ufi.domain.flight.vo.FlightIdentifier;
import ch.mobi.ufi.mailing.Notifier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
 * Workflow:
 * - je planifie l'assurance d'un vol et je regarde le tarif
 * - je confirme le contrat
 * - je suis débité sur ma carte de crédit
 * - si mon vol a du retard, je reçois une prestation
 */
@Slf4j
@RequiredArgsConstructor
public class ContractService {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);// TODO make scheduling persistent

    @NonNull
    private ContractRepository contractRepository;
    @NonNull
    private FlightService flightService;
    @NonNull
    private CompensationRepository compensationRepository;
    @NonNull
    private ChargingRepository chargingRepository;
    @NonNull
    private DefaultDocumentGenerator documentGenerator;
    @NonNull
    private Notifier notifier;


    // TODO certains vols sont déjà connus comme en avance ou en retard (expectedDate+effectiveDate+pas de status) => il faut tenir compte de l'effectiveDate s'elle  est connu
    public Contract createContract(FlightIdentifier flightIdentifier,
                                   Duration timeToCompensation) {

        // find the flight expected arrival date (fresh)
        Flight flight = flightService.findFreshFlight(flightIdentifier);

        LOG.info("got flight {}: {}", flightIdentifier, flight);

        if (flight == null) {
            throw new IllegalArgumentException("could not find flight " + flightIdentifier);
        } else if (cannotInsure(flight)) {
            throw new IllegalArgumentException("flight cannot be insure :" + flightIdentifier + " (e.g. already arrived): " + flight);
        }

        LOG.info("creating contract for flight number {}: {}", flightIdentifier, timeToCompensation);
        Contract contract = Contract.builder()
                .flightIdentifier(flightIdentifier)
                .timeToCompensation(timeToCompensation)
                .build();

        contractRepository.putContract(contract);

        notifysubscription(flightIdentifier, "1783");

        makeInvoice(contract);

        scheduleFlightArrival(contract, flight, flightIdentifier, timeToCompensation);

        return contract;
    }

    private void makeInvoice(Contract contract) {
        // send the invoice
        boolean chargingSuccess = chargingRepository.charge(Invoice.builder().build(), contract);
        if (!chargingSuccess) {
            LOG.error("failed to charge the contract, so I will not process it");
            throw new RuntimeException("failed to charge the contract, so I will not process it");
        }
    }

    private void scheduleFlightArrival(
            Contract contract,
            Flight flight,
            FlightIdentifier flightIdentifier,
            Duration timeToCompensation) {

        LocalDateTime firstRefreshDateTime = flight.getExpectedArrivalDate().plus(timeToCompensation);
        long initialDelayMs = ChronoUnit.MILLIS.between(LocalDateTime.now(), firstRefreshDateTime);

        LOG.info("scheduling flight refresh at {} ({} minutes) for {}",
                firstRefreshDateTime, Duration.ofMillis(initialDelayMs).toMinutes(), flight);
        scheduler.schedule(
                new RefreshFlightStatusRunnable(contract, flight, flightIdentifier, timeToCompensation),
                initialDelayMs,
                TimeUnit.MILLISECONDS);
    }

    private void notifysubscription(FlightIdentifier flightIdentifier, String premiumAmount) {
        VelocityContext context = new VelocityContext();
        context.put("flightId", "flightIdentifier");
        context.put("premiumAmount", premiumAmount);

        notifier.notify(
                "flight.insurance@mobi.ch",
                "olivier.vondach@obya.ch",
                "Subscription confirmation",
                documentGenerator.generate(context, "subscription.vm"));
    }

    private void notifyRefund(FlightIdentifier flightIdentifier, String insuredAmount) {
        VelocityContext context = new VelocityContext();
        context.put("flightId", "flightIdentifier");
        context.put("insuredAmount", insuredAmount);

        notifier.notify(
                "flight.insurance@mobi.ch",
                "olivier.vondach@obya.ch",
                "Refund notification",
                documentGenerator.generate(context, "refund.vm"));
    }

    // TODO potentially check effective/expected arrival date ; flight.getEffectiveArrivalDate()!=null;
    private boolean cannotInsure(Flight flight) {
        return flight.getFlightStatus() != null;
    }

    @RequiredArgsConstructor
    private class RefreshFlightStatusRunnable implements Runnable {
        final long RESCHEDULE_DELAY = 5;
        @NonNull
        Contract contract;
        @NonNull
        Flight flight;
        @NonNull
        FlightIdentifier flightIdentifier;
        @NonNull
        Duration timeToCompensation;

        @Override
        public void run() {
            // get a fresh flight data from the FlightSupplierManager
            Flight freshFlight = flightService.findFreshFlight(flightIdentifier);
            LOG.info("got fresh flight {}: {}", flightIdentifier, freshFlight);

            // if the flight is later than the contract time to compensation, pay the compensation
            LocalDateTime effectiveArrivalDate = freshFlight.getEffectiveArrivalDate();
            if (freshFlight.getFlightStatus() == null &&
                    effectiveArrivalDate != null &&
                    effectiveArrivalDate.isAfter(freshFlight.getExpectedArrivalDate().plus(timeToCompensation))) {
                // flight is arrived but is late => pay the compensation
                compensationRepository.createCompensation(contract, flight);

                notifyRefund(flight.getFlightIdentifier(), "10000");

            } else if (effectiveArrivalDate == null || freshFlight.getFlightStatus() == null) {
                // flight is not yet arrived => schedule another for later
                LOG.info("rescheduling flight refresh for {}", flight);
                scheduler.schedule(this, RESCHEDULE_DELAY, TimeUnit.MINUTES);

            } else {
                // flight is on time => no compensation to pay
                LOG.info("flight {} arrived within the {} time to compensation => no compensation to pay", freshFlight, timeToCompensation);
            }
        }
    }
}
