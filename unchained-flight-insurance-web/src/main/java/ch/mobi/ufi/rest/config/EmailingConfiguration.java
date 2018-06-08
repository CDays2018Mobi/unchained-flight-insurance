package ch.mobi.ufi.rest.config;

import ch.mobi.ufi.document.DefaultDocumentGenerator;
import ch.mobi.ufi.mailing.EmailSender;
import ch.mobi.ufi.mailing.Notifier;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailingConfiguration {

    @Autowired
    private VelocityEngine velocityEngine;

    @Bean
    public DefaultDocumentGenerator documentGenerator(VelocityEngine velocityEngine) {
        return new DefaultDocumentGenerator(velocityEngine);
    }

    @Bean
    public Notifier notifier() {
        return new EmailSender();
    }

}
