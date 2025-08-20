package uk.gov.companieshouse.monitorsubscription.matcher.serdes;

import static uk.gov.companieshouse.monitorsubscription.matcher.Application.NAMESPACE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.delta.FilingHistoryDeleteDelta;
import uk.gov.companieshouse.api.delta.FilingHistoryDelta;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.monitorsubscription.matcher.exception.NonRetryableException;
import uk.gov.companieshouse.monitorsubscription.matcher.logging.DataMapHolder;

@Component
public class FilingHistoryDeltaDeserialiser {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);
    private final ObjectMapper objectMapper;

    FilingHistoryDeltaDeserialiser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public FilingHistoryDelta deserialiseFilingHistoryDelta(String data) {
        try {
            return objectMapper.readValue(data, FilingHistoryDelta.class);
        } catch (JsonProcessingException ex) {
            LOGGER.error("Unable to deserialise delta: [%s]".formatted(data), ex, DataMapHolder.getLogMap());
            throw new NonRetryableException("Unable to deserialise delta", ex);
        }
    }

    public FilingHistoryDeleteDelta deserialiseFilingHistoryDeleteDelta(String data) {
        try {
            return objectMapper.readValue(data, FilingHistoryDeleteDelta.class);
        } catch (JsonProcessingException ex) {
            LOGGER.error("Unable to deserialise DELETE delta: [%s]".formatted(data), ex, DataMapHolder.getLogMap());
            throw new NonRetryableException("Unable to deserialise DELETE delta", ex);
        }
    }
}
