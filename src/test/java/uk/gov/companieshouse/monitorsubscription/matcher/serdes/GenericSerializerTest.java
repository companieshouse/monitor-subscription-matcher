package uk.gov.companieshouse.monitorsubscription.matcher.serdes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.buildTransactionUpdateMessage;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.NotificationMatchTestUtils.buildFilingUpdateMessage;

import consumer.exception.NonRetryableErrorException;
import java.io.IOException;
import monitor.filing;
import monitor.transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GenericSerializerTest {

    GenericSerializer underTest;

    @BeforeEach
    public void setUp() {
        underTest = new GenericSerializer();
    }

    @Test
    public void givenTransactionPayload_whenSerialized_thenByteArrayCreated() throws IOException {
        transaction payload = buildTransactionUpdateMessage().getPayload();

        byte[] result = underTest.serialize("test-topic", payload);

        assertThat(result, is(notNullValue()));
        assertThat(result.length, is(421));
    }

    @Test
    public void givenFilingPayload_whenSerialized_thenByteArrayCreated() throws IOException {
        filing payload = buildFilingUpdateMessage().getPayload();

        byte[] result = underTest.serialize("test-topic", payload);

        assertThat(result, is(notNullValue()));
        assertThat(result.length, is(692));
    }

    @Test
    public void givenByteArrayPayload_whenSerialized_thenByteArrayCreated() {
        byte[] result = underTest.serialize("test-topic", new byte[]{1, 2, 3});

        assertThat(result, is(notNullValue()));
        assertThat(result.length, is(3));
    }

    @Test
    public void givenStringPayload_whenSerialized_thenByteArrayCreated() {
        byte[] result = underTest.serialize("test-topic", "This is a test string");

        assertThat(result, is(notNullValue()));
        assertThat(result.length, is(21));
    }

    @Test
    public void givenInvalidPayload_whenSerialized_thenExceptionThrown() {
        Object invalidPayload = mock(Object.class);
        when(invalidPayload.toString()).thenThrow(new RuntimeException("toString() error"));

        NonRetryableErrorException expectedException = assertThrows(NonRetryableErrorException.class, () -> {
            underTest.serialize("test-topic", invalidPayload);
        });

        assertThat(expectedException, is(notNullValue()));
        assertThat(expectedException.getMessage(), is("Serialization exception while writing to byte array"));
    }
}