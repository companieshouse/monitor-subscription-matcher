package uk.gov.companieshouse.monitorsubscription.matcher.converter;

import java.util.function.BiFunction;
import monitor.filing;
import monitor.transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionToFilingConverter implements BiFunction<transaction, String, filing> {

    @Override
    public filing apply(final transaction transaction, final String userId) {
        return filing.newBuilder()
                .setData(transaction.getData())
                .setKind("email")
                .setNotifiedAt(transaction.getPublishedAt())
                .setUserId(userId)
                .build();
    }

}
