package uk.gov.companieshouse.monitorsubscription.matcher.consumer;

import uk.gov.companieshouse.monitorsubscription.matcher.schema.MonitorFiling;

public interface MessageRouter {

    void route(MonitorFiling message);

}
