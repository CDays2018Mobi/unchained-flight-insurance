package ch.mobi.ufi.rest.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/v1/billing")
public class DataTransEndpoint {

    @PostMapping("/cancelled")
    public ModelAndView cancelled() {
        return new ModelAndView("redirect:/index.html", Collections.emptyMap());
    }

    @PostMapping("/payed")
    public ModelAndView payed() {
        return new ModelAndView("redirect:/index.html", Collections.emptyMap());
    }
}

