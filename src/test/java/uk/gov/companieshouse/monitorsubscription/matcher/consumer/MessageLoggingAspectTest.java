package uk.gov.companieshouse.monitorsubscription.matcher.consumer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.TestUtils.buildUpdateMessage;

import java.io.IOException;
import monitor.transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class MessageLoggingAspectTest {

    @Autowired
    private MonitorFilingConsumer consumer;

    @Test
    void testAspectLogging(CapturedOutput output) throws IOException {
        Message<transaction> message = buildUpdateMessage();

        consumer.consume(message);

        // Verifies that the aspect methods were called
        assertTrue(output.getOut().contains("Processing kafka message"));
        assertTrue(output.getOut().contains("Processed kafka message"));
    }
}