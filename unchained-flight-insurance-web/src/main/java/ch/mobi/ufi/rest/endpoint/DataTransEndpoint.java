package ch.mobi.ufi.rest.endpoint;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import ch.mobi.ufi.domain.contract.service.ContractService;
import ch.mobi.ufi.domain.flight.vo.FlightIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/v1/billing")
public class DataTransEndpoint {

    @Autowired
    private ContractService contractService;

    @PostMapping("/cancelled")
    public ModelAndView cancelled(HttpServletRequest request) {
        return new ModelAndView("redirect:/billing/cancelled?" + request.getQueryString(), Collections.emptyMap());
    }

    @PostMapping("/succeeded")
    public ModelAndView payed(HttpServletRequest request) {

        String queryString = request.getQueryString();
        String[] fields = queryString.split("&");
        String[] kv;

        HashMap<String, String> things = new HashMap<String, String>();


        for (int i = 0; i < fields.length; ++i) {
            kv = fields[i].split("=");
            if (2 == kv.length) {
                things.put(kv[0], kv[1]);
            }
        }

        final String flightId = things.get("flightId");
        final String arrivalDate = things.get("expectedArrivalDate");

        contractService.createContract(
                FlightIdentifier.builder()
                        .flightNumber(flightId)
                        .flightArrivalDate(LocalDate.parse(arrivalDate, DateTimeFormatter.ISO_DATE))
                        .build(),
                Duration.ofMinutes(10));

        return new ModelAndView("redirect:/billing/succeeded?" + queryString, Collections.emptyMap());
    }
}

