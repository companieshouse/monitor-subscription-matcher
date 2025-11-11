package uk.gov.companieshouse.monitorsubscription.matcher.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.ACTIVE;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.COMPANY_NUMBER;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.CREATED_DATE;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.ID;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.INACTIVE;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.QUERY;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.MonitorFilingTestUtils.UPDATED_DATE;
import static uk.gov.companieshouse.monitorsubscription.matcher.util.NotificationMatchTestUtils.USER_ID;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.companieshouse.monitorsubscription.matcher.repository.model.MonitorQueryDocument;

@Testcontainers(disabledWithoutDocker = true)
@DataMongoTest
public class MonitorRepositoryTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MonitorRepository underTest;

    @Test
    void givenQueryDocument_whenDocumentSaved_thenRetrievedSuccessfully() {
        MonitorQueryDocument document = new MonitorQueryDocument();
        document.setId(ID);
        document.setCompanyNumber(COMPANY_NUMBER);
        document.setCreatedAt(CREATED_DATE);
        document.setActive(ACTIVE);
        document.setUpdatedAt(UPDATED_DATE);
        document.setQuery(QUERY);
        document.setUserId(USER_ID);

        underTest.save(document);

        List<MonitorQueryDocument> results = underTest.findByCompanyNumberAndIsActive(document.getCompanyNumber(), true);

        assertThat(results, is(notNullValue()));
        assertThat(results.size(), is(1));

        assertThat(ID, is(results.getFirst().getId()));
        assertThat(COMPANY_NUMBER, is(results.getFirst().getCompanyNumber()));
        assertThat(CREATED_DATE, is(results.getFirst().getCreatedAt()));
        assertThat(ACTIVE, is(results.getFirst().getActive()));
        assertThat(UPDATED_DATE, is(results.getFirst().getUpdatedAt()));
        assertThat(QUERY, is(results.getFirst().getQuery()));
        assertThat(USER_ID, is(results.getFirst().getUserId()));
    }

        @Test
    void givenQueryDocument_whenDocumentSavedAndIsInActive_thenDocumentIgnoredSuccessfully() {
        MonitorQueryDocument document = new MonitorQueryDocument();
        document.setId(ID);
        document.setCompanyNumber(COMPANY_NUMBER);
        document.setCreatedAt(CREATED_DATE);
        document.setActive(INACTIVE);
        document.setUpdatedAt(UPDATED_DATE);
        document.setQuery(QUERY);
        document.setUserId(USER_ID);    

        underTest.save(document);

        List<MonitorQueryDocument> results = underTest.findByCompanyNumberAndIsActive(document.getCompanyNumber(), true);
        assertThat(results.size(), is(0));
    }
}
