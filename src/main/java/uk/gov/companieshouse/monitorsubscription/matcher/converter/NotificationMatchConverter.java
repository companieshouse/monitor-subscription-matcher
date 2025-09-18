package uk.gov.companieshouse.monitorsubscription.matcher.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import monitor.filing;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.monitorsubscription.matcher.exception.NonRetryableException;
import uk.gov.companieshouse.monitorsubscription.matcher.producer.model.NotificationMatch;

@Component
public class NotificationMatchConverter implements Converter<NotificationMatch, filing> {

    @Override
    public filing convert(final NotificationMatch source) {
        try {
            String notificationData = new ObjectMapper().writeValueAsString(source.getData());

            return filing.newBuilder()
                    .setData(notificationData)
                    .setKind("email")
                    .setNotifiedAt(String.valueOf(System.currentTimeMillis()))
                    .setUserId(source.getUserId())
                    .build();

        } catch(JsonProcessingException ex) {
            throw new NonRetryableException("Error converting NotificationMatch to filing:", ex);
        }

    }

}
