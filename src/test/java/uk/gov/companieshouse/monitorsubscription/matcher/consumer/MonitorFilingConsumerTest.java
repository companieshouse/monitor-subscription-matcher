package uk.gov.companieshouse.monitorsubscription.matcher.consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.TestUtils.buildDeleteMessage;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.TestUtils.buildUpdateMessage;

import java.io.IOException;
import monitor.transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.monitorsubscription.matcher.converter.MonitorFilingConverter;
import uk.gov.companieshouse.monitorsubscription.matcher.exception.NonRetryableException;
import uk.gov.companieshouse.monitorsubscription.matcher.exception.RetryableException;
import uk.gov.companieshouse.monitorsubscription.matcher.consumer.model.MonitorFiling;
import uk.gov.companieshouse.monitorsubscription.matcher.service.MatcherService;

@ExtendWith(MockitoExtension.class)
public class MonitorFilingConsumerTest {

    @Mock
    MatcherService service;

    @Mock
    MonitorFilingConverter converter;

    @Mock
    MessageFlags flags;

    @Mock
    Logger logger;

    @InjectMocks
    MonitorFilingConsumer consumer;

    @InjectMocks
    MonitorFilingConsumer underTest;

    @BeforeEach
    void setUp() {
        // nothing
    }

    @Test
    @DisplayName("Given a valid update message, when consumed, then message is successful")
    void givenValidUpdateMessage_whenConsumed_thenMessageSuccessful() throws IOException {
        Message<transaction> transactionMessage = buildUpdateMessage();
        MonitorFiling filingMessage = new MonitorFilingConverter().convert(transactionMessage.getPayload());

        when(converter.convert(transactionMessage.getPayload())).thenReturn(filingMessage);

        underTest.consume(transactionMessage);

        verify(logger, times(1)).debug(anyString());
        verify(converter, times(1)).convert(transactionMessage.getPayload());
        verify(service, times(1)).processMessage(filingMessage);
    }

    @Test
    @DisplayName("Given a valid delete message, when consumed, then message is successful")
    void givenValidDeleteMessage_whenConsumed_thenMessageSuccessful() throws IOException {
        Message<transaction> transactionMessage = buildDeleteMessage();
        MonitorFiling filingMessage = new MonitorFilingConverter().convert(transactionMessage.getPayload());

        when(converter.convert(transactionMessage.getPayload())).thenReturn(filingMessage);

        underTest.consume(transactionMessage);

        verify(logger, times(1)).debug(anyString());
        verify(converter, times(1)).convert(transactionMessage.getPayload());
        verify(service, times(1)).processMessage(filingMessage);
    }

    @Test
    @DisplayName("Given an invalid JSON payload, when consumed, then non-retryable exception is thrown")
    void givenInvalidMessage_whenConsumed_thenRaiseNonRetryableException() throws IOException {
        Message<transaction> transactionMessage = buildDeleteMessage();
        when(converter.convert(transactionMessage.getPayload())).thenThrow(new NonRetryableException("test exception"));

        NonRetryableException expectedException = assertThrows(NonRetryableException.class, () -> {
            underTest.consume(transactionMessage);
        });

        verify(logger, times(1)).debug(anyString());
        verify(converter, times(1)).convert(transactionMessage.getPayload());
        verify(flags, times(1)).setRetryable(false);
        verifyNoInteractions(service);

        assertThat(expectedException.getMessage(), is("test exception"));
    }

    @Test
    @DisplayName("Given an invalid JSON payload, when consumed, then retryable exception is thrown")
    void givenInvalidMessage_whenConsumed_thenRaiseRetryableException() throws IOException {
        Message<transaction> transactionMessage = buildDeleteMessage();
        when(converter.convert(transactionMessage.getPayload())).thenThrow(new RetryableException("test exception", new IOException()));

        RetryableException expectedException = assertThrows(RetryableException.class, () -> {
            underTest.consume(transactionMessage);
        });

        verify(logger, times(1)).debug(anyString());
        verify(converter, times(1)).convert(transactionMessage.getPayload());
        verify(flags, times(1)).setRetryable(true);
        verifyNoInteractions(service);

        assertThat(expectedException.getMessage(), is("test exception"));
    }

}
