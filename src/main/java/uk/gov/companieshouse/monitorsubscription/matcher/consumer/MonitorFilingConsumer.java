package uk.gov.companieshouse.monitorsubscription.matcher.consumer;

import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.monitorsubscription.matcher.exception.RetryableException;
import uk.gov.companieshouse.monitorsubscription.matcher.logging.DataMapHolder;

@Component
public class MonitorFilingConsumer {

    private final MessageRouter router;
    private final Logger logger;

    public MonitorFilingConsumer(MessageRouter router, Logger logger) {
        this.router = router;
        this.logger = logger;
    }

    @KafkaListener(
            containerFactory = "kafkaListenerContainerFactory",
            id = "${spring.kafka.consumer.filing.group-id}",
            topics = {"${spring.kafka.consumer.filing.topic}"},
            groupId = "${spring.kafka.consumer.filing.group-id}"
    )
    public void consume(final GenericRecord message) {
        logger.debug("********************************************************************************************");
        logger.debug("consume(message=%s) method called.".formatted(message));
        try {
            logger.debug("Received message: %s".formatted(message.toString()));

            //router.route(message.getPayload());

        } catch (RetryableException ex) {
            logger.error("Retryable exception encountered processing message.", ex, DataMapHolder.getLogMap());
            throw ex;

        } catch (Exception ex) {
            logger.error("Non-Retryable exception encountered processing message!", ex, DataMapHolder.getLogMap());
            throw new RuntimeException(ex);
        }
        logger.debug("********************************************************************************************");
    }
}
