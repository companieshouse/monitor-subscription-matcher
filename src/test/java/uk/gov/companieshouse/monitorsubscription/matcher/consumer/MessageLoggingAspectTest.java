package uk.gov.companieshouse.monitorsubscription.matcher.consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.buildTransactionUpdateMessage;

import java.util.HashMap;
import java.util.Map;
import monitor.transaction;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import uk.gov.companieshouse.logging.Logger;

@ExtendWith(MockitoExtension.class)
class MessageLoggingAspectTest {

    @Mock
    Logger logger;

    MessageLoggingAspect underTest;

    @BeforeEach
    void setUp() {
        underTest = new MessageLoggingAspect(logger);
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenValidMessage_whenLogBeforeMainConsumerCalled_thenUseValues() {
        Map<String, Object> kafkaMessageHeaders = new HashMap<>();
        kafkaMessageHeaders.put(KafkaHeaders.RECEIVED_TOPIC, "test-kafka-topic");
        kafkaMessageHeaders.put(KafkaHeaders.RECEIVED_PARTITION, 0);
        kafkaMessageHeaders.put(KafkaHeaders.OFFSET, 45L);
        kafkaMessageHeaders.put(KafkaHeaders.CORRELATION_ID, "test-correlation-id");

        Message<transaction> kafkaMessage = buildTransactionUpdateMessage();
        GenericMessage<transaction> message = new GenericMessage<>(kafkaMessage.getPayload(), kafkaMessageHeaders);

        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{message});

        underTest.logBeforeMainConsumer(joinPoint);

        verify(logger, times(1)).debug(eq("Processing kafka message"), any(Map.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenValidMessage_whenLogAfterMainConsumerCalled_thenUseValues() {
        Map<String, Object> kafkaMessageHeaders = new HashMap<>();
        kafkaMessageHeaders.put(KafkaHeaders.RECEIVED_TOPIC, "test-kafka-topic");
        kafkaMessageHeaders.put(KafkaHeaders.RECEIVED_PARTITION, 0);
        kafkaMessageHeaders.put(KafkaHeaders.OFFSET, 45L);
        kafkaMessageHeaders.put(KafkaHeaders.CORRELATION_ID, "test-correlation-id");

        Message<transaction> kafkaMessage = buildTransactionUpdateMessage();
        GenericMessage<transaction> message = new GenericMessage<>(kafkaMessage.getPayload(), kafkaMessageHeaders);

        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{message});

        underTest.logAfterMainConsumer(joinPoint);

        verify(logger, times(1)).debug(eq("Processed kafka message"), any(Map.class));
    }
}