package uk.gov.companieshouse.monitorsubscription.matcher.service;

import java.util.List;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.monitorsubscription.matcher.model.MonitorFiling;
import uk.gov.companieshouse.monitorsubscription.matcher.repository.model.MonitorQueryDocument;
import uk.gov.companieshouse.monitorsubscription.matcher.repository.MonitorRepository;

/**
 * Default message consumer to allow the build to compile. We need at least one qualifying bean with
 * the MessageRouter interface in order for the build to compile, but this can be removed later on.
 */
@Service
public class MatcherService {

    private final MonitorRepository repository;
    private final Logger logger;

    public MatcherService(final MonitorRepository repository, final Logger logger) {
        this.repository = repository;
        this.logger = logger;
    }

    public void processMessage(final MonitorFiling message) {
        logger.trace("processMessage(message=%s) method called.".formatted(message));

        // Processing to be added here for matching...
        List<MonitorQueryDocument> companies = repository.findByCompanyNumber(message.getCompanyNumber());
        logger.debug("Found %d matching companies".formatted(companies.size()));

    }
}
