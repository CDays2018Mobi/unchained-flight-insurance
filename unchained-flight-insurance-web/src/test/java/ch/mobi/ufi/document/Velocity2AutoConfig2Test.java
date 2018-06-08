package ch.mobi.ufi.document;

import org.apache.velocity.app.VelocityEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
@TestPropertySource(properties = {
        "velocity.enabled=false",
        "velocity.toolbox.enabled=false"
})
public class Velocity2AutoConfig2Test {

    @Autowired(required = false)
    private VelocityEngine velocityEngine;

    @Test
    public void startEnvironment_test() throws IOException {
        assertNull(velocityEngine);
    }

}
