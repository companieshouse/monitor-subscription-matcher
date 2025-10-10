package uk.gov.companieshouse.monitorsubscription.matcher.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.fail;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.PUBLISHED_AT;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.buildTransactionDeleteMessage;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.buildTransactionEmptyDataMessage;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.buildTransactionNullDataMessage;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.buildTransactionUpdateMessage;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.NotificationMatchTestUtils.KIND;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.NotificationMatchTestUtils.USER_ID;

import monitor.filing;
import monitor.transaction;
import org.apache.avro.AvroRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;

@ExtendWith(MockitoExtension.class)
class TransactionToFilingConverterTest {

    TransactionToFilingConverter underTest;

    @BeforeEach
    void setUp() {
       underTest = new TransactionToFilingConverter();
    }

    @Test
    void givenValidUpdatePayload_whenConverted_thenFilingCreated() {
        Message<transaction> message = buildTransactionUpdateMessage();

        Message<filing> result = underTest.apply(message.getPayload(), USER_ID);
        assertThat(result, is(notNullValue()));

        filing payload = result.getPayload();
        assertThat(payload, is(notNullValue()));
        assertThat(payload.getData(), is(notNullValue()));
        assertThat(payload.getKind(), is(KIND));
        assertThat(payload.getNotifiedAt(), is(PUBLISHED_AT));
        assertThat(payload.getUserId(), is(USER_ID));
    }

    @Test
    void givenValidDeletePayload_whenConverted_thenFilingCreated() {
        Message<transaction> message = buildTransactionDeleteMessage();

        Message<filing> result = underTest.apply(message.getPayload(), USER_ID);
        assertThat(result, is(notNullValue()));

        filing payload = result.getPayload();
        assertThat(payload, is(notNullValue()));
        assertThat(payload.getData(), is(notNullValue()));
        assertThat(payload.getKind(), is(KIND));
        assertThat(payload.getNotifiedAt(), is(PUBLISHED_AT));
        assertThat(payload.getUserId(), is(USER_ID));
    }

    @Test
    void givenEmptyPayload_whenConverted_thenFilingCreated() {
        Message<transaction> message = buildTransactionEmptyDataMessage();

        Message<filing> result = underTest.apply(message.getPayload(), USER_ID);
        assertThat(result, is(notNullValue()));

        filing payload = result.getPayload();
        assertThat(payload, is(notNullValue()));
        assertThat(payload.getData(), is(emptyOrNullString()));
        assertThat(payload.getKind(), is(KIND));
        assertThat(payload.getNotifiedAt(), is(PUBLISHED_AT));
        assertThat(payload.getUserId(), is(USER_ID));
    }

    @Test
    void givenNullPayload_whenConverted_thenFilingCreated() {
        try {
            buildTransactionNullDataMessage();

            fail("An AvroRuntimeException should have been thrown!");

        } catch(AvroRuntimeException ex) {
            // Exception expected due to null data in Avro object
            assertThat(ex.getMessage(), is("Field data type:STRING pos:1 does not accept null values"));
        }
    }
}
