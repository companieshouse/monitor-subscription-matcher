package uk.gov.companieshouse.monitorsubscription.matcher.consumer;

import java.util.Optional;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.util.DataMap;

/**
 * Logs message details before and after it has been processed by
 * the {@link MonitorFilingConsumer main consumer}.<br>
 * <br>
 * Details that will be logged will include:
 * <ul>
 *     <li>The context ID of the message</li>
 *     <li>The topic the message was consumed from</li>
 *     <li>The partition of the topic the message was consumed from</li>
 *     <li>The offset number of the message</li>
 * </ul>
 */
@Component
@Aspect
public class MessageLoggingAspect {

    private static final String LOG_MESSAGE_RECEIVED = "Processing kafka message";
    private static final String LOG_MESSAGE_PROCESSED = "Processed kafka message";

    private final Logger logger;

    public MessageLoggingAspect(Logger logger) {
        this.logger = logger;
    }

    @Before("execution(* uk.gov.companieshouse.monitorsubscription.matcher.consumer.MonitorFilingConsumer.consume(..))")
    void logBeforeMainConsumer(JoinPoint joinPoint) {
        logMessage(LOG_MESSAGE_RECEIVED, (Message<?>)joinPoint.getArgs()[0]);
    }

    @After("execution(* uk.gov.companieshouse.monitorsubscription.matcher.consumer.MonitorFilingConsumer.consume(..))")
    void logAfterMainConsumer(JoinPoint joinPoint) {
        logMessage(LOG_MESSAGE_PROCESSED, (Message<?>)joinPoint.getArgs()[0]);
    }

    private void logMessage(String logMessage, Message<?> incomingMessage) {
        MessageHeaders messageHeaders = incomingMessage.getHeaders();

        var topic = Optional.ofNullable((String) messageHeaders.get(KafkaHeaders.RECEIVED_TOPIC)).orElse("no topic");
        var partition = Optional.of((Integer) messageHeaders.get(KafkaHeaders.RECEIVED_PARTITION)).orElse(0);
        var offset = Optional.of((Long) messageHeaders.get(KafkaHeaders.OFFSET)).orElse(0L);

        var dataMap = new DataMap.Builder()
                .topic(topic)
                .partition(partition)
                .offset(offset)
                .kafkaMessage(incomingMessage.getPayload().toString())
                .build();

        logger.debug(logMessage, dataMap.getLogMap());
    }
}

