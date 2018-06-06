package ch.mobi.ufi.rest.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;

@Slf4j
@Configuration
public class ClientSidePathLocationStrategySupportConfiguration {

    /*
     * Collaboration behaviour for Angular PathLocationStrategy
     * https://codecraft.tv/courses/angular/routing/routing-strategies/
     */
    @Bean
    ErrorViewResolver supportPathLocationStrategy() {
        LOG.info("supporting 404");
        return (request, status, model) -> status == HttpStatus.NOT_FOUND
                ? new ModelAndView("index.html", Collections.emptyMap(), HttpStatus.OK)
                : null;
    }
}
