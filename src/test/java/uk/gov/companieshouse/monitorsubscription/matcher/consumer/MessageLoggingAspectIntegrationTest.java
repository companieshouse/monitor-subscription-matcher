package uk.gov.companieshouse.monitorsubscription.matcher.consumer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import monitor.transaction;
import uk.gov.companieshouse.monitorsubscription.matcher.service.MatcherService;
import uk.gov.companieshouse.monitorsubscription.matcher.util.DisabledIfDockerUnavailable;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.buildTransactionUpdateMessage;

@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@DisabledIfDockerUnavailable
class MessageLoggingAspectIntegrationTest {

    @Autowired
    private MonitorFilingConsumer consumer;

    @MockitoBean
    private MatcherService matcherService;

    @Test
    void testAspectLogging(CapturedOutput output) {
        Message<transaction> message = buildTransactionUpdateMessage();

        consumer.consume(message);
        verify(matcherService).processMessage(message.getPayload());

        // Verifies that the aspect methods were called
        assertTrue(output.getOut().contains("Processing kafka message"));
        assertTrue(output.getOut().contains("Processed kafka message"));
    }
}