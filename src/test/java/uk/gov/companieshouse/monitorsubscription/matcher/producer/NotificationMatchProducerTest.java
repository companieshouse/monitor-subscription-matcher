package uk.gov.companieshouse.monitorsubscription.matcher.producer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.NotificationMatchTestUtils.buildNotificationMatchFromUpdateMessage;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.monitorsubscription.matcher.config.properties.NotificationMatchProducerProperties;
import uk.gov.companieshouse.monitorsubscription.matcher.converter.NotificationMatchConverter;
import uk.gov.companieshouse.monitorsubscription.matcher.producer.model.NotificationMatch;

@ExtendWith(MockitoExtension.class)
public class NotificationMatchProducerTest {

    @Mock
    NotificationMatchProducerProperties properties;

    @Mock
    KafkaTemplate<String, Object> template;

    @Mock
    NotificationMatchConverter converter;

    @Mock
    Logger logger;

    NotificationMatchProducer underTest;

    @BeforeEach
    public void setUp() {
        underTest = new NotificationMatchProducer(properties, template, converter, logger);
    }

    @Test
    public void givenValidUpdateMessage_whenSendMessage_thenNoErrorsAreRaised() throws IOException {
        NotificationMatch message = buildNotificationMatchFromUpdateMessage();

        underTest.sendMessage(message);

        verify(logger, times(1)).trace("sendMessage(message=%s) method called.".formatted(message));

        verifyNoInteractions(template);
        verifyNoInteractions(converter);
    }
}
