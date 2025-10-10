package uk.gov.companieshouse.monitorsubscription.matcher.producer;

import monitor.filing;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.monitorsubscription.matcher.config.properties.NotificationMatchProducerProperties;

@Component
public class NotificationMatchProducer {

    private final NotificationMatchProducerProperties properties;
    private final KafkaTemplate<String, Object> template;
    private final Logger logger;

    public NotificationMatchProducer(final NotificationMatchProducerProperties properties,
            final KafkaTemplate<String, Object> template, final Logger logger) {
        this.properties = properties;
        this.template = template;
        this.logger = logger;
    }

    public void sendMessage(final Message<filing> message) {
        logger.trace("sendMessage(message=%s) method called.".formatted(message));

        /*
        If we attempt to push the existing message directly to Kafka, we may encounter serialization issues. The
        message contains its own headers which can conflict with Kafka's serialization process. To avoid this, we
        extract the payload and submit that part only - and the template will handle the serialization and headers.
        However, we may lose our Correlation ID in the process - and this needs to be dealt with if it becomes a
        problem tracking messages through the service-stack.
        */

        // Send the converted message to the Kafka topic
        template.send(properties.getTopic(), message.getPayload());
    }

}
