package uk.gov.companieshouse.monitorsubscription.matcher.consumer;

import monitor.transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.SameIntervalTopicReuseStrategy;
import org.springframework.messaging.Message;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.monitorsubscription.matcher.converter.MonitorFilingConverter;
import uk.gov.companieshouse.monitorsubscription.matcher.exception.RetryableException;
import uk.gov.companieshouse.monitorsubscription.matcher.logging.DataMapHolder;
import uk.gov.companieshouse.monitorsubscription.matcher.model.MonitorFiling;
import uk.gov.companieshouse.monitorsubscription.matcher.service.MatcherService;

@Component
public class Consumer {

    private final MatcherService service;
    private final MonitorFilingConverter converter;
    private final MessageFlags messageFlags;
    private final Logger logger;

    /**
     * Mandatory constructor.
     * @param service the service to delegate message processing to.
     * @param converter the converter to convert the message payload.
     * @param messageFlags flags to indicate the type of message being processed.
     * @param logger the logger to use for logging.
     */
    public Consumer(MatcherService service, MonitorFilingConverter converter, MessageFlags messageFlags, Logger logger) {
        this.service = service;
        this.converter = converter;
        this.messageFlags = messageFlags;
        this.logger = logger;
    }

    /**
     * Consume a message from the main Kafka topic.
     * @param message A message containing a payload.
     */
    @KafkaListener(
            id = "${spring.kafka.consumer.filing.group-id}",
            containerFactory = "kafkaListenerContainerFactory",
            topics = "${spring.kafka.consumer.filing.topic}",
            groupId = "${spring.kafka.consumer.filing.group-id}",
            autoStartup = "true"
    )
    @RetryableTopic(
            attempts = "${spring.kafka.consumer.filing.max-attempts}",
            autoCreateTopics = "false",
            backoff = @Backoff(delayExpression = "${spring.kafka.consumer.filing.backoff-delay}"),
            dltTopicSuffix = "-error",
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            sameIntervalTopicReuseStrategy = SameIntervalTopicReuseStrategy.SINGLE_TOPIC,
            include = RetryableException.class,
            kafkaTemplate = "kafkaTemplate"
    )

    public void consume(final Message<transaction> message) {
        logger.debug("consume(message=%s) method called.".formatted(message));
        try {
            MonitorFiling monitorFiling = converter.convert(message.getPayload());
            service.processMessage(monitorFiling);

        } catch (RetryableException ex) {
            logger.error("Retryable exception encountered processing message.", ex, DataMapHolder.getLogMap());
            messageFlags.setRetryable(true);
            throw ex;
        }
    }
}
