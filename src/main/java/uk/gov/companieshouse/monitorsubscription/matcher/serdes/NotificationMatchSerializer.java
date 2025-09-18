package uk.gov.companieshouse.monitorsubscription.matcher.serdes;

import static java.lang.String.format;
import static uk.gov.companieshouse.monitorsubscription.matcher.config.ApplicationConfig.NAMESPACE;

import consumer.exception.NonRetryableErrorException;
import monitor.filing;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.serialization.Serializer;
import uk.gov.companieshouse.kafka.serialization.AvroSerializer;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.monitorsubscription.matcher.logging.DataMapHolder;

public class NotificationMatchSerializer implements Serializer<filing> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

    @Override
    public byte[] serialize(final String topic, final filing payload) {
        LOGGER.trace(format("serialize() -> [Topic: %s, Payload: %s]", topic, payload.getClass().getSimpleName()));

        try {
            DatumWriter<filing> writer = new SpecificDatumWriter<>();
            EncoderFactory encoderFactory = EncoderFactory.get();

            AvroSerializer<filing> avroSerializer = new AvroSerializer<>(writer, encoderFactory);
            return avroSerializer.toBinary(payload);

        } catch (Exception ex) {
            LOGGER.error("Serialization exception while writing to byte array", ex, DataMapHolder.getLogMap());
            throw new NonRetryableErrorException("Serialization exception while writing to byte array", ex);
        }
    }

}
