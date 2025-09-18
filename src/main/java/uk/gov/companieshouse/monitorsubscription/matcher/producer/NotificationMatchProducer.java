package uk.gov.companieshouse.monitorsubscription.matcher.producer;

import monitor.filing;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.monitorsubscription.matcher.config.properties.NotificationMatchProducerProperties;
import uk.gov.companieshouse.monitorsubscription.matcher.converter.NotificationMatchConverter;
import uk.gov.companieshouse.monitorsubscription.matcher.producer.model.NotificationMatch;

@Component
public class NotificationMatchProducer {

    private final NotificationMatchProducerProperties properties;
    private final KafkaTemplate<String, filing> template;
    private final NotificationMatchConverter converter;
    private final Logger logger;

    public NotificationMatchProducer(NotificationMatchProducerProperties properties,
            KafkaTemplate<String, filing> template,
            NotificationMatchConverter converter,
            Logger logger) {
        this.properties = properties;
        this.template = template;
        this.converter = converter;
        this.logger = logger;
    }

    public void sendMessage(final NotificationMatch message) {
        logger.trace("sendMessage(message=%s) method called.".formatted(message));

        // Convert the message to the required format before sending
        filing filing = converter.convert(message);

        // Send the converted message to the Kafka topic
        template.send(properties.getTopic(), filing);
    }

}
