package uk.gov.companieshouse.monitorsubscription.matcher.repository.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MonitorQueryDocumentTest {

    private static final String ID = "654321";
    private static final String COMPANY_NUMBER = "00006400";
    private static final LocalDateTime CREATED_DATE = LocalDateTime.parse("2023-10-10T10:00:00");
    private static final Boolean ACTIVE = Boolean.TRUE;
    private static final LocalDateTime UPDATED_DATE = CREATED_DATE.plusDays(1);
    private static final String QUERY = "QUERY transaction WHERE company_number=\"%s\"".formatted(COMPANY_NUMBER);
    private static final String USER_ID = "";

    MonitorQueryDocument underTest;

    @BeforeEach
    void setUp() {
        underTest = new MonitorQueryDocument();
    }

    @Test
    void givenEmptyModel_whenValuesAreSet_thenTheyAreRetrievedSuccessfully() {
        underTest.setId(ID);
        underTest.setCompanyNumber(COMPANY_NUMBER);
        underTest.setCreatedAt(CREATED_DATE);
        underTest.setActive(ACTIVE);
        underTest.setUpdatedAt(UPDATED_DATE);
        underTest.setQuery(QUERY);
        underTest.setUserId(USER_ID);

        assertThat(underTest.getId(), is(ID));
        assertThat(underTest.getCompanyNumber(), is(COMPANY_NUMBER));
        assertThat(underTest.getCreatedAt(), is(CREATED_DATE));
        assertThat(underTest.getActive(), is(ACTIVE));
        assertThat(underTest.getUpdatedAt(), is(UPDATED_DATE));
        assertThat(underTest.getQuery(), is(QUERY));
        assertThat(underTest.getUserId(), is(USER_ID));
    }
}
