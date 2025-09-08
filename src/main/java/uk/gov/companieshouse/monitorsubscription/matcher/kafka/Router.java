package uk.gov.companieshouse.monitorsubscription.matcher.kafka;

import uk.gov.companieshouse.monitorsubscription.matcher.consumer.MonitorFilingMessage;

public interface Router {

    void route(MonitorFilingMessage delta);

}
