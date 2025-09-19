package uk.gov.companieshouse.monitorsubscription.matcher.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.buildTransactionDeleteMessage;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.buildTransactionDeleteMessageWithIgnoredFields;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.buildTransactionEmptyDataMessage;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.buildTransactionInvalidMessage;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.buildTransactionUpdateMessage;

import java.io.IOException;
import monitor.transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.monitorsubscription.matcher.exception.NonRetryableException;
import uk.gov.companieshouse.monitorsubscription.matcher.consumer.model.MonitorFiling;

@ExtendWith(MockitoExtension.class)
public class MonitorFilingConverterTest {

    MonitorFilingConverter underTest;

    @BeforeEach
    public void setUp() {
        underTest = new MonitorFilingConverter();
    }

    @Test
    public void givenValidUpdatePayload_whenConverted_thenMonitorFilingCreated() throws IOException {
        transaction transaction = buildTransactionUpdateMessage().getPayload();

        MonitorFiling result = underTest.convert(transaction);

        assertThat(result, is(notNullValue()));

        assertThat(result.getCompanyNumber(), is("00006400"));
        assertThat(result.getData(), is(notNullValue()));
        assertThat(result.getPublishedAt(), is("2025-03-03T15:04:03"));
        assertThat(result.getVersion(), is("0"));
        assertThat(result.getOffset(), is(""));

        assertThat(result.getData().getIsDelete(), is(false));
    }

    @Test
    public void givenValidDeletePayload_whenConverted_thenMonitorFilingCreated() throws IOException {
        transaction transaction = buildTransactionDeleteMessage().getPayload();

        MonitorFiling result = underTest.convert(transaction);

        assertThat(result, is(notNullValue()));

        assertThat(result.getCompanyNumber(), is("00006400"));
        assertThat(result.getData(), is(notNullValue()));
        assertThat(result.getPublishedAt(), is("2025-03-03T15:04:03"));
        assertThat(result.getVersion(), is("0"));
        assertThat(result.getOffset(), is(""));

        assertThat(result.getData().getIsDelete(), is(true));
    }

    @Test
    public void givenValidDeletePayload_whenConverted_thenVerifyNoExtraPropertiesProvided() throws IOException {
        transaction transaction = buildTransactionDeleteMessageWithIgnoredFields().getPayload();

        MonitorFiling result = underTest.convert(transaction);

        assertThat(result, is(notNullValue()));

        assertThat(result.getCompanyNumber(), is("00006400"));
        assertThat(result.getData(), is(notNullValue()));
        assertThat(result.getPublishedAt(), is("2025-03-03T15:04:03"));
        assertThat(result.getVersion(), is("0"));
        assertThat(result.getOffset(), is(""));

        assertThat(result.getData().getIsDelete(), is(true));

        assertThat(result.getAdditionalProperties(), is(aMapWithSize(0)));
        assertThat(result.getData().getAdditionalProperties(), is(aMapWithSize(1)));
        assertThat(result.getData().getData().getAdditionalProperties(), is(aMapWithSize(1)));
        assertThat(result.getData().getData().getDescriptionValues().getAdditionalProperties(), is(aMapWithSize(1)));
    }


    @Test
    public void givenEmptyDataPayload_whenConverted_thenMonitorFilingCreated() throws IOException {
        transaction transaction = buildTransactionEmptyDataMessage().getPayload();

        MonitorFiling result = underTest.convert(transaction);

        assertThat(result, is(notNullValue()));

        assertThat(result.getCompanyNumber(), is("00006400"));
        assertThat(result.getData(), is(nullValue()));
        assertThat(result.getPublishedAt(), is("2025-03-03T15:04:03"));
        assertThat(result.getVersion(), is("0"));
        assertThat(result.getOffset(), is(""));
    }

    @Test
    public void givenInvalidPayload_whenConverted_thenMonitorFilingCreated() throws IOException {
        transaction transaction = buildTransactionInvalidMessage().getPayload();

        NonRetryableException expectedException = assertThrows(NonRetryableException.class, () -> {
            underTest.convert(transaction);
        });

        assertThat(expectedException.getMessage(), is("Error converting transaction to MonitorFiling:"));
    }
}
