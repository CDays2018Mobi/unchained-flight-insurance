package ch.mobi.ufi.document;

import ch.mobi.ufi.mailing.Notifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
@TestPropertySource(properties = {
        "velocity.enabled=true",
        "velocity.toolbox.enabled=false"
})
public class DocumentGenerationTest {

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private DefaultDocumentGenerator documentGenerator;

    @Autowired
    private Notifier notifier;

    @Test
    public void generate_test() throws IOException {
        assertNotNull(velocityEngine);

        VelocityContext context = new VelocityContext();
        context.put("flightId", "ESY914");
        context.put("premiumAmount", "25.00");
        context.put("insuredAmount", "500.00");

        notifier.notify(
                "olivier.vondach@obya.ch",
                "didier.damiot@mobi.ch",
                "test",
                documentGenerator.generate(context, "subscription.vm"));
    }
}
