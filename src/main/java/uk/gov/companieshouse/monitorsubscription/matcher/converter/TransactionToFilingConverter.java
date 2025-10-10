package uk.gov.companieshouse.monitorsubscription.matcher.converter;

import java.util.function.BiFunction;
import monitor.filing;
import monitor.transaction;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.monitorsubscription.matcher.logging.DataMapHolder;

@Component
public class TransactionToFilingConverter implements BiFunction<transaction, String, Message<filing>> {

    public static final String EMAIL_KIND = "email";

    @Override
    public Message<filing> apply(final transaction transaction, final String userId) {
        filing payload =  filing.newBuilder()
                .setData(transaction.getData())
                .setKind(EMAIL_KIND)
                .setNotifiedAt(transaction.getPublishedAt())
                .setUserId(userId)
                .build();

        return MessageBuilder.withPayload(payload)
                .setHeader(KafkaHeaders.CORRELATION_ID, DataMapHolder.getRequestId())
                .build();
    }

}
