package uk.gov.companieshouse.monitorsubscription.matcher.converter;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import monitor.filing;
import monitor.transaction;
import org.apache.commons.lang3.StringUtils;
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
                .setHeader(KafkaHeaders.CORRELATION_ID, getCorrelationId())
                .build();
    }

    private String getCorrelationId() {
        Optional<String> correlationId = Optional.ofNullable(DataMapHolder.getRequestId());
        if(correlationId.isEmpty() || StringUtils.equals(correlationId.get(), "uninitialised")) {
            return UUID.randomUUID().toString();
        }
        return correlationId.get();
    }
}
