package uk.gov.companieshouse.monitorsubscription.matcher.service;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.monitorsubscription.matcher.model.MonitorFiling;

/**
 * Default message consumer to allow the build to compile. We need at least one qualifying bean with the MessageRouter
 * interface in order for the build to compile, but this can be removed later on.
 */
@Service
public class MatcherService {

    private final Logger logger;

    public MatcherService(final Logger logger) {
        this.logger = logger;
    }

    public void processMessage(final MonitorFiling filing) {
        logger.debug("processMessage() method called.");

        // Processing to be added here for matching...
    }
}
