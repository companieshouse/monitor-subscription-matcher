package uk.gov.companieshouse.monitorsubscription.matcher.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.buildMonitorFilingFromUpdateMessage;

import java.io.IOException;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.monitorsubscription.matcher.consumer.model.MonitorFiling;
import uk.gov.companieshouse.monitorsubscription.matcher.producer.NotificationMatchProducer;
import uk.gov.companieshouse.monitorsubscription.matcher.repository.MonitorRepository;

@ExtendWith(MockitoExtension.class)
public class MatcherServiceTest {

    @Mock
    MonitorRepository repository;

    @Mock
    NotificationMatchProducer producer;

    @Mock
    Logger logger;

    MatcherService underTest;

    @BeforeEach
    public void setUp() {
        underTest = new MatcherService(repository, producer, logger);
    }

    @Test
    public void givenUpdateMessage_whenProcessed_thenNoErrorsRaised() throws IOException {
        MonitorFiling message = buildMonitorFilingFromUpdateMessage();

        when(repository.findByCompanyNumber(message.getCompanyNumber())).thenReturn(Collections.emptyList());

        underTest.processMessage(message);

        verify(logger, times(1)).trace(anyString());
        verify(repository, times(1)).findByCompanyNumber(message.getCompanyNumber());
        verify(logger, times(1)).debug(anyString());
    }
}
