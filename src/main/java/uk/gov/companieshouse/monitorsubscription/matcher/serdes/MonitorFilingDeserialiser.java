package uk.gov.companieshouse.monitorsubscription.matcher.serdes;

import static java.lang.String.format;
import static uk.gov.companieshouse.monitorsubscription.matcher.config.ApplicationConfig.NAMESPACE;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.kafka.common.serialization.Deserializer;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.monitorsubscription.matcher.exception.InvalidPayloadException;
import uk.gov.companieshouse.monitorsubscription.matcher.logging.DataMapHolder;
import uk.gov.companieshouse.monitorsubscription.matcher.schema.MonitorFiling;

public class MonitorFilingDeserialiser implements Deserializer<MonitorFiling> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

    @Override
    public MonitorFiling deserialize(final String topic, byte[] data) {
        LOGGER.debug(format("deserialize() -> [Topic: %s, Payload: %d bytes]", topic, data.length));

        try {
            String messageData = new String(data, StandardCharsets.UTF_8);
            LOGGER.debug(messageData);

            Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            DatumReader<MonitorFiling> reader = new ReflectDatumReader<>(MonitorFiling.class);
            return reader.read(null, decoder);

        } catch (IOException | AvroRuntimeException ex) {
            String payload = new String(data);
            LOGGER.error("Error deserialising message payload: [%s]".formatted(payload), ex, DataMapHolder.getLogMap());
            throw new InvalidPayloadException("Invalid payload: [%s]".formatted(payload), ex);
        }
    }
}
