package ch.mobi.ufi.rest.endpoint;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/v1/billing")
public class DataTransEndpoint {

    @PostMapping("/cancelled")
    public ModelAndView cancelled(HttpServletRequest request) {
        return new ModelAndView("redirect:/billing/cancelled?" + request.getQueryString(), Collections.emptyMap());
    }

    @PostMapping("/payed")
    public ModelAndView payed(HttpServletRequest request) {
        return new ModelAndView("redirect:/billing/succeeded?" + request.getQueryString(), Collections.emptyMap());
    }
}

