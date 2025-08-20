package uk.gov.companieshouse.monitorsubscription.matcher.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.delta.ChsDelta;
import uk.gov.companieshouse.monitorsubscription.matcher.exception.RetryableException;

@Component
public class Consumer {

    private final Router router;
    private final MessageFlags messageFlags;

    public Consumer(Router router, MessageFlags messageFlags) {
        this.router = router;
        this.messageFlags = messageFlags;
    }

    @KafkaListener(
            id = "${consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory",
            topics = {"${consumer.topic}"},
            groupId = "${consumer.group-id}"
    )
    public void consume(final Message<ChsDelta> message) {
        try {
            router.route(message.getPayload());

        } catch (RetryableException ex) {
            messageFlags.setRetryable(true);
            throw ex;
        }
    }
}
