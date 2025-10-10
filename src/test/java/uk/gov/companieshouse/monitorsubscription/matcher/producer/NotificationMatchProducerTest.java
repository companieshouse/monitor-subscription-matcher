package uk.gov.companieshouse.monitorsubscription.matcher.producer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.NotificationMatchTestUtils.buildFilingDeleteMessage;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.NotificationMatchTestUtils.buildFilingUpdateMessage;

import monitor.filing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.monitorsubscription.matcher.config.properties.NotificationMatchProducerProperties;

@ExtendWith(MockitoExtension.class)
class NotificationMatchProducerTest {

    @Mock
    NotificationMatchProducerProperties properties;

    @Mock
    KafkaTemplate<String, Object> template;

    @Mock
    Logger logger;

    NotificationMatchProducer underTest;

    @BeforeEach
    void setUp() {
        underTest = new NotificationMatchProducer(properties, template, logger);
    }

    @Test
    void givenValidUpdateMessage_whenSendMessage_thenNoErrorsAreRaised() {
        Message<filing> message = buildFilingUpdateMessage();

        when(properties.getTopic()).thenReturn("test-topic");

        underTest.sendMessage(message);

        verify(logger, times(1)).trace("sendMessage(message=%s) method called.".formatted(message));
        verify(template, times(1)).send("test-topic", message);
    }

    @Test
    void givenValidDeleteMessage_whenSendMessage_thenNoErrorsAreRaised() {
        Message<filing> message = buildFilingDeleteMessage();
        when(properties.getTopic()).thenReturn("test-topic");

        underTest.sendMessage(message);

        verify(logger, times(1)).trace("sendMessage(message=%s) method called.".formatted(message));
        verify(template, times(1)).send("test-topic", message);
    }
}
