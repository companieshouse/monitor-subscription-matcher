package uk.gov.companieshouse.monitorsubscription.matcher.kafka;

import static java.lang.String.format;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.monitorsubscription.matcher.consumer.MonitorFilingMessage;
import uk.gov.companieshouse.monitorsubscription.matcher.exception.RetryableException;
import uk.gov.companieshouse.monitorsubscription.matcher.logging.DataMapHolder;
import uk.gov.companieshouse.monitorsubscription.matcher.schema.MonitorFiling;

@Component
public class Consumer {

    private final Router router;
    private final MessageFlags messageFlags;
    private final Logger logger;

    public Consumer(Router router, MessageFlags messageFlags, Logger logger) {
        this.router = router;
        this.messageFlags = messageFlags;
        this.logger = logger;
    }

    @KafkaListener(
            containerFactory = "kafkaListenerContainerFactory",
            id = "${kafka.consumer.filing.group-id}",
            topics = {"${kafka.consumer.filing.topic}"},
            groupId = "${kafka.consumer.filing.group-id}"
    )
    public void consume(final Message<MonitorFiling> message) {
        logger.debug("********************************************************************************************");
        logger.debug(format("consume(message=%s) method called.", message));
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            //String jsonValue = writer.writeValueAsString(message.getPayload());
            String jsonValue = writer.writeValueAsString(message);

            logger.debug(jsonValue);

            //router.route(message.getPayload());
            router.route(message.getPayload());

        } catch (RetryableException ex) {
            logger.error("Retryable exception encountered while processing message. Will retry.",
                    ex, DataMapHolder.getLogMap()
            );
            messageFlags.setRetryable(true);
            throw ex;

        } catch (Exception ex) {
            logger.error("Non-Retryable exception encountered while processing message!",
                    ex, DataMapHolder.getLogMap()
            );
            messageFlags.setRetryable(false);
            throw new RuntimeException(ex);
        }
        logger.debug("********************************************************************************************");
    }
}
