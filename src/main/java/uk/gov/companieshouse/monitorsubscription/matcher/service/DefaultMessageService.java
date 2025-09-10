package uk.gov.companieshouse.monitorsubscription.matcher.service;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.monitorsubscription.matcher.consumer.MessageRouter;
import uk.gov.companieshouse.monitorsubscription.matcher.schema.MonitorFiling;

/**
 * Default message consumer to allow the build to compile. We need at least one qualifying bean with the MessageRouter
 * interface in order for the build to compile, but this can be removed later on.
 */
@Service
public class DefaultMessageService implements MessageRouter {

    private final Logger logger;

    public DefaultMessageService(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public void route(final MonitorFiling message) {
        logger.debug(String.format("route(message=%s) method called.", message));

        // Message processing goes here.
    }
}
