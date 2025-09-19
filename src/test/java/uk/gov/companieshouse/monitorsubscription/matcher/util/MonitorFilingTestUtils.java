package uk.gov.companieshouse.monitorsubscription.matcher.util;

import static org.springframework.kafka.support.KafkaHeaders.EXCEPTION_CAUSE_FQCN;

import com.fasterxml.jackson.databind.ObjectMapper;
import consumer.exception.NonRetryableErrorException;
import consumer.serialization.AvroSerializer;
import java.io.IOException;
import java.time.LocalDateTime;
import monitor.transaction;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import uk.gov.companieshouse.monitorsubscription.matcher.consumer.model.MonitorFiling;

public class MonitorFilingTestUtils {

    public static final String ID = "654321";
    public static final String COMPANY_NUMBER = "00006400";
    public static final LocalDateTime CREATED_DATE = LocalDateTime.parse("2023-10-10T10:00:00");
    public static final Boolean ACTIVE = Boolean.TRUE;
    public static final LocalDateTime UPDATED_DATE = CREATED_DATE.plusDays(1);
    public static final String QUERY = "QUERY transaction WHERE company_number=\"%s\"".formatted(COMPANY_NUMBER);
    public static final String USER_ID = "";

    public static final String MONITOR_FILING_UPDATE_MESSAGE = """
            {
              "company_number": "00006400",
              "data": {
                "company_number": "00006400",
                "data": {
                  "type": "AP01",
                  "transaction_id": "158153-915517-386847",
                  "description" : "appoint-person-director-company-with-name-date",
                  "description_values" : {
                    "appointment_date" : "1 December 2024",
                    "officer_name" : "DR AMIDAT DUPE IYIOLA"
                  },
                  "date": "2025-02-04"
                },
                "is_delete": false
              },
              "published_at": "2025-03-03T15:04:03",
              "version": "0",
              "offset": "2121212121"
            }
            """;

    public static final String MONITOR_FILING_DELETE_MESSAGE = """
            {
              "company_number": "00006400",
              "data": {
                "company_number": "00006400",
                "data": {
                  "type": "AP01",
                  "transaction_id": "158153-915517-386847",
                  "description" : "appoint-person-director-company-with-name-date",
                  "description_values" : {
                    "appointment_date" : "1 December 2024",
                    "officer_name" : "DR AMIDAT DUPE IYIOLA"
                  },
                  "date": "2025-02-04"
                },
                "is_delete": true
              },
              "published_at": "2025-03-03T15:04:03",
              "version": "0",
              "offset": "2121212121"
            }
            """;

    public static final String MONITOR_FILING_DELETE_MESSAGE_WITH_IGNORED_FIELDS = """
            {
              "company_number": "00006400",
              "data": {
                "company_number": "00006400",
                "data": {
                  "type": "AP01",
                  "transaction_id": "158153-915517-386847",
                  "description" : "appoint-person-director-company-with-name-date",
                  "description_values" : {
                    "appointment_date" : "1 December 2024",
                    "officer_name" : "DR AMIDAT DUPE IYIOLA",
                    "extra_field_4": "This field should be ignored"
                  },
                  "date": "2025-02-04",
                  "extra_field_3": "This field should be ignored"
                },
                "is_delete": true,
                "extra_field_2": "This field should also be ignored"
              },
              "published_at": "2025-03-03T15:04:03",
              "version": "0",
              "offset": "2121212121",
              "extra_field_1": "This field should also be ignored"
            }
            """;

    private static transaction buildTransactionWithData(final String data) {
        return transaction.newBuilder()
                .setCompanyNumber("00006400")
                .setData(data)
                .setPublishedAt("2025-03-03T15:04:03")
                .setVersion("0")
                .build();
    }

    public static Message<transaction> buildTransactionUpdateMessage() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        MonitorFiling model = mapper.readValue(MONITOR_FILING_UPDATE_MESSAGE, MonitorFiling.class);

        String dataString = mapper.writeValueAsString(model.getData());

        return MessageBuilder
                .withPayload(buildTransactionWithData(dataString))
                .setHeader("kafka_receivedTopic", "test-topic")
                .setHeader("kafka_offset", 42L)  // optional
                .build();
    }

    public static Message<transaction> buildTransactionDeleteMessage() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        MonitorFiling model = mapper.readValue(MONITOR_FILING_DELETE_MESSAGE, MonitorFiling.class);

        String dataString = mapper.writeValueAsString(model.getData());

        return MessageBuilder
                .withPayload(buildTransactionWithData(dataString))
                .setHeader("kafka_receivedTopic", "test-topic")
                .setHeader("kafka_offset", 42L)  // optional
                .build();
    }

    public static Message<transaction> buildTransactionDeleteMessageWithIgnoredFields() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        MonitorFiling model = mapper.readValue(MONITOR_FILING_DELETE_MESSAGE_WITH_IGNORED_FIELDS, MonitorFiling.class);

        String dataString = mapper.writeValueAsString(model.getData());

        return MessageBuilder
                .withPayload(buildTransactionWithData(dataString))
                .setHeader("kafka_receivedTopic", "test-topic")
                .setHeader("kafka_offset", 42L)  // optional
                .build();
    }

    public static Message<transaction> buildTransactionInvalidMessage() {
        String dataString = "This is NOT valid JSON data";

        return MessageBuilder
                .withPayload(buildTransactionWithData(dataString))
                .setHeader("kafka_receivedTopic", "test-topic")
                .setHeader("kafka_offset", 42L)  // optional
                .build();
    }

    public static Message<transaction> buildTransactionEmptyDataMessage() {
        String dataString = "";

        return MessageBuilder
                .withPayload(buildTransactionWithData(dataString))
                .setHeader("kafka_receivedTopic", "test-topic")
                .setHeader("kafka_offset", 42L)  // optional
                .build();
    }

    public static Message<transaction> buildTransactionMessageWithExceptionCauseHeader() {
        String dataString = "";

        return MessageBuilder
                .withPayload(buildTransactionWithData(dataString))
                .setHeader(EXCEPTION_CAUSE_FQCN, new RecordHeader("exception-cause-key", NonRetryableErrorException.class.getName().getBytes()))
                .setHeader("kafka_receivedTopic", "test-topic")
                .setHeader("kafka_offset", 42L)  // optional
                .build();
    }


    public static byte[] buildTransactionRawAvroMessage() throws IOException {
        return new AvroSerializer().serialize("test-topic", buildTransactionUpdateMessage().getPayload());
    }

    public static MonitorFiling buildMonitorFilingFromUpdateMessage() throws IOException {
        return new ObjectMapper().readValue(MONITOR_FILING_UPDATE_MESSAGE, MonitorFiling.class);
    }
}
