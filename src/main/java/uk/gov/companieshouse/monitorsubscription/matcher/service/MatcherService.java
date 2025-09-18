package uk.gov.companieshouse.monitorsubscription.matcher.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.monitorsubscription.matcher.consumer.model.MonitorFiling;
import uk.gov.companieshouse.monitorsubscription.matcher.producer.NotificationMatchProducer;
import uk.gov.companieshouse.monitorsubscription.matcher.repository.model.MonitorQueryDocument;
import uk.gov.companieshouse.monitorsubscription.matcher.repository.MonitorRepository;

/**
 * Default message consumer to allow the build to compile. We need at least one qualifying bean with
 * the MessageRouter interface in order for the build to compile, but this can be removed later on.
 */
@Service
public class MatcherService {

    private final MonitorRepository repository;
    private final NotificationMatchProducer producer;
    private final Logger logger;

    public MatcherService(MonitorRepository repository, NotificationMatchProducer producer, Logger logger) {
        this.repository = repository;
        this.producer = producer;
        this.logger = logger;
    }

    public void processMessage(final MonitorFiling message) {
        logger.trace("processMessage(message=%s) method called.".formatted(message));

        // Processing to be added here for matching...
        List<MonitorQueryDocument> companies = repository.findByCompanyNumber(message.getCompanyNumber());
        logger.debug("Found %d matching companies".formatted(companies.size()));

        if(companies.isEmpty()) {
            //logger.debug("No matching companies found, no more processing required...");
            return;
        }

        // Finally, send a message to the target topic.
        producer.sendMessage(null);

        //logger.debug("Message sent to Notification Match topic.");
    }

    private Optional<String> getTransactionId(final MonitorFiling message) {
        logger.trace("getTransactionId() method called.");

        if (message.getData() == null || message.getData().getData() == null || message.getData().getData().getTransactionId().isEmpty()) {
            logger.debug("Warning: No Transaction ID was identified within the given message!");
            return Optional.empty();
        }

        return Optional.of(message.getData().getData().getTransactionId());
    }
}
