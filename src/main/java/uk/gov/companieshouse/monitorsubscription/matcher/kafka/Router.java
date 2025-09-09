package uk.gov.companieshouse.monitorsubscription.matcher.kafka;

import uk.gov.companieshouse.monitorsubscription.matcher.schema.MonitorFiling;

public interface Router {

    void route(MonitorFiling message);

}
