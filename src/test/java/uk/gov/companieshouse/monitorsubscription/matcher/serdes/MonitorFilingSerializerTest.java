package uk.gov.companieshouse.monitorsubscription.matcher.serdes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.TestUtils.buildUpdateMessage;

import consumer.exception.NonRetryableErrorException;
import java.io.IOException;
import monitor.transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MonitorFilingSerializerTest {

    MonitorFilingSerializer underTest;

    @BeforeEach
    public void setUp() {
        underTest = new MonitorFilingSerializer();
    }

    @Test
    public void givenTransactionPayload_whenSerialized_thenByteArrayCreated() throws IOException {
        transaction payload = buildUpdateMessage().getPayload();

        byte[] result = underTest.serialize("test-topic", payload);

        assertThat(result, is(notNullValue()));
        assertThat(result.length, is(325));
    }

    @Test
    public void givenByteArrayPayload_whenSerialized_thenByteArrayCreated() {
        byte[] result = underTest.serialize("test-topic", new byte[]{1, 2, 3});

        assertThat(result, is(notNullValue()));
        assertThat(result.length, is(3));
    }

    @Test
    public void givenStringPayload_whenSerialized_thenByteArrayCreated() {
        byte[] result = underTest.serialize("test-topic", new String("This is a test string"));

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
