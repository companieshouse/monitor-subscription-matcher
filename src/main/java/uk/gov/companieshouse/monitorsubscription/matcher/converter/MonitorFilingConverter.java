package uk.gov.companieshouse.monitorsubscription.matcher.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.transaction;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.monitorsubscription.matcher.exception.NonRetryableException;
import uk.gov.companieshouse.monitorsubscription.matcher.consumer.model.MonitorFiling;
import uk.gov.companieshouse.monitorsubscription.matcher.consumer.model.MonitorFilingData;

@Component
public class MonitorFilingConverter implements Converter<transaction, MonitorFiling> {

    @Override
    public MonitorFiling convert(final transaction source) {
        MonitorFiling target = new MonitorFiling();
        target.setCompanyNumber(source.getCompanyNumber());

        try {
            if(StringUtils.isNotEmpty(source.getData())) {
                MonitorFilingData metadata = new ObjectMapper().readValue(source.getData(), MonitorFilingData.class);
                target.setData(metadata);
            }

        } catch(JsonProcessingException ex) {
            throw new NonRetryableException("Error converting transaction to MonitorFiling:", ex);
        }

        target.setPublishedAt(source.getPublishedAt());
        target.setVersion(source.getVersion());
        target.setOffset("");

        return target;
    }

}
