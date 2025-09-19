package uk.gov.companieshouse.monitorsubscription.matcher.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import monitor.filing;
import monitor.transaction;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import uk.gov.companieshouse.monitorsubscription.matcher.producer.model.NotificationMatch;

public class NotificationMatchTestUtils {

    public static final String KIND = "email";
    public static final String NOTIFIED_AT = "1453896192000";
    public static final String USER_ID = "1vKD26OwehmZI6MpGz9D02-dmCI";

    public static final String NOTIFICATION_MATCH_UPDATE_MESSAGE = """
            {
              "data": {
                "app_id": "chs-monitor-notification-matcher.filing",
                "company_number": "00006400",
                "data": {
                  "type": "AP01",
                  "description" : "appoint-person-director-company-with-name-date",
                  "description_values" : {
                    "appointment_date" : "1 December 2024",
                    "officer_name" : "DR AMIDAT DUPE IYIOLA"
                  },
                  "links" : {
                    "self" : "/transactions/158153-915517-386847/officers/67a2396e8e70c90c76a3ba62"
                  },
                  "category": "officers",
                  "paper_filed": false,
                  "subcategory": "appointments",
                  "action_date": "2025-02-04",
                  "date": "2025-02-04"
                },
                "is_delete": false
              },
              "kind": "email",
              "notified_at": "1453896192000",
              "user_id": "1vKD26OwehmZI6MpGz9D02-dmCI"
            }
            """;

    public static final String NOTIFICATION_MATCH_DELETE_MESSAGE = """
            {
              "data": {
                "app_id": "chs-monitor-notification-matcher.filing",
                "company_number": "00006400",
                "data": {
                  "type": "AP01",
                  "description" : "appoint-person-director-company-with-name-date",
                  "description_values" : {
                    "appointment_date" : "1 December 2024",
                    "officer_name" : "DR AMIDAT DUPE IYIOLA"
                  },
                  "links" : {
                    "self" : "/transactions/158153-915517-386847/officers/67a2396e8e70c90c76a3ba62"
                  },
                  "category": "officers",
                  "paper_filed": false,
                  "subcategory": "appointments",
                  "action_date": "2025-02-04",
                  "date": "2025-02-04"
                },
                "is_delete": true
              },
              "kind": "email",
              "notified_at": "1453896192000",
              "user_id": "1vKD26OwehmZI6MpGz9D02-dmCI"
            }
            """;

    public static final String NOTIFICATION_MATCH_DELETE_MESSAGE_WITH_IGNORED_FIELDS = """
            {
              "data": {
                "app_id": "chs-monitor-notification-matcher.filing",
                "company_number": "00006400",
                "data": {
                  "type": "AP01",
                  "description" : "appoint-person-director-company-with-name-date",
                  "description_values" : {
                    "appointment_date" : "1 December 2024",
                    "officer_name" : "DR AMIDAT DUPE IYIOLA",
                    "extra_field_4": "should be ignored"
                  },
                  "links" : {
                    "self" : "/transactions/158153-915517-386847/officers/67a2396e8e70c90c76a3ba62",
                    "extra_field_5": "should be ignored"
                  },
                  "category": "officers",
                  "paper_filed": false,
                  "subcategory": "appointments",
                  "action_date": "2025-02-04",
                  "date": "2025-02-04",
                  "extra_field_3": "should be ignored"
                },
                "is_delete": true,
                "extra_field_2": "should be ignored"
              },
              "kind": "email",
              "notified_at": "1453896192000",
              "user_id": "1vKD26OwehmZI6MpGz9D02-dmCI",
              "extra_field_1": "should be ignored"
            }
            """;

    private static filing buildFilingWithData(final String data) {
        return filing.newBuilder()
                .setData(data)
                .setKind(KIND)
                .setNotifiedAt(NOTIFIED_AT)
                .setUserId(USER_ID)
                .build();
    }

    public static Message<filing> buildFilingUpdateMessage() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        NotificationMatch model = mapper.readValue(NOTIFICATION_MATCH_UPDATE_MESSAGE, NotificationMatch.class);

        String dataString = mapper.writeValueAsString(model.getData());

        return MessageBuilder
                .withPayload(buildFilingWithData(dataString))
                .setHeader("kafka_receivedTopic", "test-topic")
                .setHeader("kafka_offset", 42L)  // optional
                .build();

    }

    public static Message<filing> buildFilingDeleteMessage() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        NotificationMatch model = mapper.readValue(NOTIFICATION_MATCH_DELETE_MESSAGE, NotificationMatch.class);

        String dataString = mapper.writeValueAsString(model.getData());

        return MessageBuilder
                .withPayload(buildFilingWithData(dataString))
                .setHeader("kafka_receivedTopic", "test-topic")
                .setHeader("kafka_offset", 42L)  // optional
                .build();

    }

    public static NotificationMatch buildNotificationMatchFromUpdateMessage() throws IOException {
        return new ObjectMapper().readValue(NOTIFICATION_MATCH_UPDATE_MESSAGE, NotificationMatch.class);
    }

    public static NotificationMatch buildNotificationMatchFromDeleteMessage() throws IOException {
        return new ObjectMapper().readValue(NOTIFICATION_MATCH_DELETE_MESSAGE, NotificationMatch.class);
    }

    public static NotificationMatch buildNotificationMatchFromDeleteMessageWithIgnoredFields() throws IOException {
        return new ObjectMapper().readValue(NOTIFICATION_MATCH_DELETE_MESSAGE_WITH_IGNORED_FIELDS, NotificationMatch.class);
    }
}
