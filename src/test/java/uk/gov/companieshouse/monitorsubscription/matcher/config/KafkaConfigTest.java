package uk.gov.companieshouse.monitorsubscription.matcher.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import monitor.transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.monitorsubscription.matcher.config.properties.MonitorFilingConsumerProperties;

@ExtendWith(MockitoExtension.class)
public class KafkaConfigTest {

    private MonitorFilingConsumerProperties monitorFilingProperties;
    private KafkaConfig underTest;

    @BeforeEach
    public void setUp() {
        monitorFilingProperties = new MonitorFilingConsumerProperties();
        monitorFilingProperties.setTopic("test-topic");
        monitorFilingProperties.setGroupId("test-group");
        monitorFilingProperties.setConcurrency(1);
        monitorFilingProperties.setMaxAttempts(3);
        monitorFilingProperties.setBackOffDelay(1000L);

        String bootstrapServers = "localhost:9092";
        Logger logger = LoggerFactory.getLogger("test-logger");

        underTest = new KafkaConfig(monitorFilingProperties, bootstrapServers, logger);
    }

    @Test
    public void givenKafkaConfigProperties_whenLoaded_thenValuesAreSet() {
        assertThat(monitorFilingProperties, is(notNullValue()));

        assertThat(monitorFilingProperties.getTopic(), is("test-topic"));
        assertThat(monitorFilingProperties.getGroupId(), is("test-group"));
        assertThat(monitorFilingProperties.getConcurrency(), is(1));
        assertThat(monitorFilingProperties.getMaxAttempts(), is(3));
        assertThat(monitorFilingProperties.getBackOffDelay(), is(1000L));
    }

    @Test
    public void givenConfigProvider_whenKafkaTemplateCreated_thenNoErrorsAreRaised() {
        KafkaTemplate<String, Object> result = underTest.kafkaTemplate();

        assertThat(result, is(notNullValue()));
    }

    @Test
    public void givenConfigProvider_whenKafkaConsumerFactoryCreated_thenNoErrorsAreRaised() {
        ConsumerFactory<String, transaction> result = underTest.kafkaConsumerFactory();

        assertThat(result, is(notNullValue()));
    }

    @Test
    public void givenConfigProvider_whenKafkaListenerContainerFactoryCreated_thenNoErrorsAreRaised() {
        ConcurrentKafkaListenerContainerFactory<String, transaction> result = underTest.kafkaListenerContainerFactory();

        assertThat(result, is(notNullValue()));
    }
}
