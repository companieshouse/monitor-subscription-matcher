package uk.gov.companieshouse.monitorsubscription.matcher.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.buildTransactionUpdateMessage;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import monitor.transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.companieshouse.monitorsubscription.matcher.consumer.MonitorFilingConsumer;

@SpringBootTest
@EmbeddedKafka(partitions = 1,
        topics = { "test-topic" },
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092" }
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class KafkaIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private MonitorFilingConsumer consumer;

    private final CountDownLatch latch = new CountDownLatch(1);
    private transaction receivedMessage;

    @BeforeEach
    void setup() {
        consumer.setCallback(message -> {
            receivedMessage = message;
            latch.countDown();
        });
    }

    @Test
    void testMessageIsConsumed() throws IOException, InterruptedException {
        transaction message = buildTransactionUpdateMessage().getPayload();

        kafkaTemplate.send("test-topic", message);

        boolean messageConsumed = latch.await(10, TimeUnit.SECONDS);

        String expectedMessage = "{\"company_number\": \"00006400\", \"data\": \"{\\\"company_number\\\":\\\"00006400\\\",\\\"data\\\":{\\\"type\\\":\\\"AP01\\\",\\\"transaction_id\\\":\\\"158153-915517-386847\\\",\\\"description\\\":\\\"appoint-person-director-company-with-name-date\\\",\\\"description_values\\\":{\\\"appointment_date\\\":\\\"1 December 2024\\\",\\\"officer_name\\\":\\\"DR AMIDAT DUPE IYIOLA\\\"},\\\"date\\\":\\\"2025-02-04\\\"},\\\"is_delete\\\":false}\", \"published_at\": \"2025-03-03T15:04:03\", \"version\": \"0\"}";

        assertTrue(messageConsumed, "Message was not consumed in time");
        assertThat(423, is(expectedMessage.length()));
        assertThat(423, is(receivedMessage.toString().length()));

        assertThat(receivedMessage.toString(), is(expectedMessage));
    }
}
