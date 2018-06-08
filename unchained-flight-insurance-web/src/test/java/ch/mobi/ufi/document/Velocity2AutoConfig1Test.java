package ch.mobi.ufi.document;

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
import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
@TestPropertySource(properties = {
        "velocity.enabled=true",
        "velocity.toolbox.enabled=false"
})
public class Velocity2AutoConfig1Test {

    @Autowired
    private VelocityEngine velocityEngine;

    @Test
    public void startEnvironment_test() throws IOException {
        assertNotNull(velocityEngine);

        VelocityContext context = new VelocityContext();
        context.put("foo", "World");
        StringWriter sw = new StringWriter();
        boolean eval = velocityEngine.evaluate(context, sw, "unitTest1", "Hello $foo");

        assertTrue("String not evaluated", eval);
        String result = sw.toString();
        LOG.info("evaluated: " + result);
        assertThat(result, is(equalTo("Hello World")));

        sw.close();
    }
}
