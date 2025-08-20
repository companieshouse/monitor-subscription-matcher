package uk.gov.companieshouse.monitorsubscription.matcher.kafka;

import uk.gov.companieshouse.delta.ChsDelta;

public interface Router {

    void route(ChsDelta delta);

}
