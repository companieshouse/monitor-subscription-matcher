package uk.gov.companieshouse.monitorsubscription.matcher.serdes;

import static uk.gov.companieshouse.monitorsubscription.matcher.config.ApplicationConfig.NAMESPACE;

import consumer.exception.NonRetryableErrorException;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.monitorsubscription.matcher.logging.DataMapHolder;
import uk.gov.companieshouse.monitorsubscription.matcher.schema.MonitorFiling;

@Component
public class MonitorFilingDeserialiser implements Deserializer<MonitorFiling> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

    @Override
    public MonitorFiling deserialize(String topic, byte[] data) {
        LOGGER.trace("deserialize() -> [Topic: %s, Data: %d bytes]".formatted(topic, data.length));

        try {
            Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            DatumReader<MonitorFiling> reader = new ReflectDatumReader<>(MonitorFiling.class);
            MonitorFiling monitorFiling = reader.read(null, decoder);

            LOGGER.info("Message successfully de-serialised", DataMapHolder.getLogMap());

            return monitorFiling;

        } catch (Exception ex) {
            LOGGER.error("De-Serialization exception while converting to Avro schema object", ex, DataMapHolder.getLogMap());
            throw new NonRetryableErrorException("De-Serialization exception while converting to Avro schema object", ex);
        }
    }
}
