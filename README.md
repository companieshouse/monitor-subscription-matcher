# monitor-subscription-matcher

> Part of Monitor/Follow system. Identifies which users want to be notified of filings taken place for a particular
company.

### Service Overview
- Consumes messages from the `monitor-filing` Kafka topic.
- Matches the entries in the `queries` collection in the MongoDB `monitor` database.
- Produces messages to the `notification-match` Kafka topic for each matched entry.

### Incoming Messages (Consumed)
- *Incoming messages are consumed from a Kafka Topic named*: `monitor-filing`
  - Headers:
    - `correlation_id`: UUIDv4 string
    - `reply_to`: Kafka topic name to send response to
  - Body:
    ```json
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
    ```
    
### Outgoing Messages (Produced)
- *Outgoing messages are produced to a Kafka Topic named*: `notification-match`
  - Headers:
    - `correlation_id`: (Taken and inserted from the consumed message)
    - `reply_to`: Kafka topic name to send response to
  - Body:
    ```json
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
        "notified_at": "2025-03-03T15:04:03",
        "user_id": "1vKD26OwehmZI6MpGz9D02-dmCI"
    }
    ```
