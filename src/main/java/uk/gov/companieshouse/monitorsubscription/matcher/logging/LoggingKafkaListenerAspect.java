package uk.gov.companieshouse.monitorsubscription.matcher.logging;

import static org.springframework.kafka.retrytopic.RetryTopicHeaders.DEFAULT_HEADER_ATTEMPTS;
import static org.springframework.kafka.support.KafkaHeaders.OFFSET;
import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_PARTITION;
import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_TOPIC;
import static uk.gov.companieshouse.monitorsubscription.matcher.config.ApplicationConfig.NAMESPACE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.UUID;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.monitorsubscription.matcher.consumer.model.Payload;
import uk.gov.companieshouse.monitorsubscription.matcher.exception.NonRetryableException;
import uk.gov.companieshouse.monitorsubscription.matcher.exception.RetryableException;
import uk.gov.companieshouse.monitorsubscription.matcher.schema.MonitorFiling;

//@Component
//@Aspect
class LoggingKafkaListenerAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

    private static final String LOG_MESSAGE_RECEIVED = "Processing delta";
    private static final String LOG_MESSAGE_DELETE_RECEIVED = "Processing DELETE delta";
    private static final String LOG_MESSAGE_PROCESSED = "Processed delta";
    private static final String LOG_MESSAGE_DELETE_PROCESSED = "Processed DELETE delta";
    private static final String EXCEPTION_MESSAGE = "%s exception thrown";

    private final int maxAttempts;

    LoggingKafkaListenerAspect(@Value("${consumer.max-attempts}") int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    //@Around("@annotation(org.springframework.kafka.annotation.KafkaListener)")
    public Object manageStructuredLogging(final ProceedingJoinPoint joinPoint) throws Throwable {
        LOGGER.debug("manageStructuredLogging() method called");

        int retryCount = 0;
        try {
            Message<?> message = (Message<?>) joinPoint.getArgs()[0];
            MessageHeaders headers = message.getHeaders();

            retryCount = Optional.ofNullable(headers.get(DEFAULT_HEADER_ATTEMPTS))
                    .map(attempts -> ByteBuffer.wrap((byte[]) attempts).getInt())
                    .orElse(1) - 1;

            MonitorFiling monitorFiling = extractTopicMessage(message.getPayload());
            Payload payload = new ObjectMapper().readValue(monitorFiling.getData().toString(), Payload.class);

            // TODO: Get the contextId from the MonitorFilingMessage when available
            String contextId = UUID.randomUUID().toString();
            DataMapHolder.initialise(Optional.ofNullable(contextId).orElse(UUID.randomUUID().toString()));

            DataMapHolder.get()
                    .retryCount(retryCount)
                    .topic((String) headers.get(RECEIVED_TOPIC))
                    .partition((Integer) headers.get(RECEIVED_PARTITION))
                    .offset((Long) headers.get(OFFSET));

            boolean isDelete = payload.getIsDelete() != null && payload.getIsDelete();

            LOGGER.info(isDelete ? LOG_MESSAGE_DELETE_RECEIVED : LOG_MESSAGE_RECEIVED,
                    DataMapHolder.getLogMap());

            Object result = joinPoint.proceed();

            LOGGER.info(isDelete ? LOG_MESSAGE_DELETE_PROCESSED : LOG_MESSAGE_PROCESSED,
                    DataMapHolder.getLogMap());

            return result;

        } catch (RetryableException ex) {
            // maxAttempts includes first attempt which is not a retry
            if (retryCount >= maxAttempts - 1) {
                LOGGER.error("Max retry attempts reached", ex, DataMapHolder.getLogMap());
            } else {
                LOGGER.info(EXCEPTION_MESSAGE.formatted(ex.getClass().getSimpleName()), DataMapHolder.getLogMap());
            }
            throw ex;

        } catch (Exception ex) {
            LOGGER.error("Exception thrown", ex, DataMapHolder.getLogMap());
            throw ex;

        } finally {
            DataMapHolder.clear();
        }
    }

    private MonitorFiling extractTopicMessage(final Object payload) {
        LOGGER.trace("extractTopicMessage(payload=%d bytes) method called".formatted(payload.toString().getBytes().length));
        try {
            LOGGER.debug(new ObjectMapper().writeValueAsString(payload));

            if (payload instanceof MonitorFiling message) {
                return message;
            }

            String errorMessage = "Invalid payload type, payload: [%s]".formatted(payload.toString());
            LOGGER.error(errorMessage, DataMapHolder.getLogMap());
            throw new NonRetryableException(errorMessage);

        } catch(JsonProcessingException ex) {
            String errorMessage = "Error processing payload: [%s]".formatted(payload.toString());
            LOGGER.error(errorMessage, ex, DataMapHolder.getLogMap());
            throw new NonRetryableException(errorMessage, ex);
        }
    }
}