package uk.gov.companieshouse.monitorsubscription.matcher.producer.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NotificationMatchModelTest {

    NotificationMatch underTest;

    @BeforeEach
    void setUp() {
        underTest = new NotificationMatch();
    }

}

