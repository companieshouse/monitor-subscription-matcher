package uk.gov.companieshouse.monitorsubscription.matcher.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.NotificationMatchTestUtils.KIND;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.NotificationMatchTestUtils.NOTIFIED_AT;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.NotificationMatchTestUtils.USER_ID;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.NotificationMatchTestUtils.buildNotificationMatchFromDeleteMessage;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.NotificationMatchTestUtils.buildNotificationMatchFromDeleteMessageWithIgnoredFields;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.NotificationMatchTestUtils.buildNotificationMatchFromUpdateMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import monitor.filing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.monitorsubscription.matcher.exception.NonRetryableException;
import uk.gov.companieshouse.monitorsubscription.matcher.producer.model.NotificationMatch;

@ExtendWith(MockitoExtension.class)
public class NotificationMatchConverterTest {

    NotificationMatchConverter underTest;

    @BeforeEach
    public void setUp() {
       underTest = new NotificationMatchConverter(new ObjectMapper());
    }

    @Test
    public void givenValidUpdatePayload_whenConverted_thenMonitorFilingCreated() throws IOException {
        NotificationMatch model = buildNotificationMatchFromUpdateMessage();

        filing result = underTest.convert(model);

        assertThat(result, is(notNullValue()));

        assertThat(result.getData(), is(notNullValue()));
        assertThat(result.getKind(), is(KIND));
        assertThat(result.getNotifiedAt(), is(NOTIFIED_AT));
        assertThat(result.getUserId(), is(USER_ID));
    }

    @Test
    public void givenValidDeletePayload_whenConverted_thenMonitorFilingCreated() throws IOException {
        NotificationMatch model = buildNotificationMatchFromDeleteMessage();

        filing result = underTest.convert(model);

        assertThat(result, is(notNullValue()));

        assertThat(result.getData(), is(notNullValue()));
        assertThat(result.getKind(), is(KIND));
        assertThat(result.getNotifiedAt(), is(NOTIFIED_AT));
        assertThat(result.getUserId(), is(USER_ID));
    }

    @Test
    public void givenValidDeletePayload_whenConverted_thenVerifyNoExtraPropertiesProvided() throws IOException {
        NotificationMatch model = buildNotificationMatchFromDeleteMessageWithIgnoredFields();

        filing result = underTest.convert(model);

        assertThat(result, is(notNullValue()));

        assertThat(result.getData(), is(notNullValue()));
        assertThat(result.getKind(), is(KIND));
        assertThat(result.getNotifiedAt(), is(NOTIFIED_AT));
        assertThat(result.getUserId(), is(USER_ID));
    }

    @Test
    public void givenInvalidPayload_whenConverted_thenNonRetryableExceptionRaised() throws IOException {
        ObjectMapper mockMapper = mock(ObjectMapper.class);
        when(mockMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

        NotificationMatch model = buildNotificationMatchFromUpdateMessage();

        NonRetryableException expectedException = assertThrows(NonRetryableException.class, () -> {
            NotificationMatchConverter mockTest = new NotificationMatchConverter(mockMapper);
            mockTest.convert(model);
        });

        assertThat(expectedException.getMessage(), is("Error converting NotificationMatch to filing:"));
    }

}
