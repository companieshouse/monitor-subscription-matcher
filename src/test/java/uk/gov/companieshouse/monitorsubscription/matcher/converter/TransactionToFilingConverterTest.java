package uk.gov.companieshouse.monitorsubscription.matcher.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.PUBLISHED_AT;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.buildTransactionDeleteMessage;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.buildTransactionEmptyDataMessage;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.buildTransactionUpdateMessage;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.NotificationMatchTestUtils.KIND;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.NotificationMatchTestUtils.USER_ID;

import java.io.IOException;
import monitor.filing;
import monitor.transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;

@ExtendWith(MockitoExtension.class)
public class TransactionToFilingConverterTest {

    TransactionToFilingConverter underTest;

    @BeforeEach
    public void setUp() {
       underTest = new TransactionToFilingConverter();
    }

    @Test
    public void givenValidUpdatePayload_whenConverted_thenFilingCreated() throws IOException {
        Message<transaction> message = buildTransactionUpdateMessage();

        filing result = underTest.apply(message.getPayload(), USER_ID);

        assertThat(result, is(notNullValue()));

        assertThat(result.getData(), is(notNullValue()));
        assertThat(result.getKind(), is(KIND));
        assertThat(result.getNotifiedAt(), is(PUBLISHED_AT));
        assertThat(result.getUserId(), is(USER_ID));
    }

    @Test
    public void givenValidDeletePayload_whenConverted_thenFilingCreated() throws IOException {
        Message<transaction> message = buildTransactionDeleteMessage();

        filing result = underTest.apply(message.getPayload(), USER_ID);

        assertThat(result, is(notNullValue()));

        assertThat(result.getData(), is(notNullValue()));
        assertThat(result.getKind(), is(KIND));
        assertThat(result.getNotifiedAt(), is(PUBLISHED_AT));
        assertThat(result.getUserId(), is(USER_ID));
    }

    @Test
    public void givenEmptyPayload_whenConverted_thenFilingCreated() throws IOException {
        Message<transaction> message = buildTransactionEmptyDataMessage();

        filing result = underTest.apply(message.getPayload(), USER_ID);

        assertThat(result, is(notNullValue()));

        assertThat(result.getData(), is(emptyOrNullString()));
        assertThat(result.getKind(), is(KIND));
        assertThat(result.getNotifiedAt(), is(PUBLISHED_AT));
        assertThat(result.getUserId(), is(USER_ID));
    }
}
