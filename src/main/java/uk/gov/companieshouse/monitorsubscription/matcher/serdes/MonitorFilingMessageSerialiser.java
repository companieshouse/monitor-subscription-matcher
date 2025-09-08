package uk.gov.companieshouse.monitorsubscription.matcher.serdes;

import static java.lang.String.format;
import static uk.gov.companieshouse.monitorsubscription.matcher.config.ApplicationConfig.NAMESPACE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.kafka.common.serialization.Serializer;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.monitorsubscription.matcher.consumer.MonitorFilingMessage;
import uk.gov.companieshouse.monitorsubscription.matcher.exception.NonRetryableException;

public class MonitorFilingMessageSerialiser implements Serializer<MonitorFilingMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

    @Override
    public byte[] serialize(final String topic, final MonitorFilingMessage data) {
        LOGGER.debug(format("serialise() -> [Topic: %s, Data: %s]", topic, data));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Encoder encoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        DatumWriter<MonitorFilingMessage> writer = getDatumWriter();
        try {
            writer.write(data, encoder);
        } catch (IOException ex) {
            throw new NonRetryableException("Error serialising delta", ex);
        }
        return outputStream.toByteArray();
    }

    public DatumWriter<MonitorFilingMessage> getDatumWriter() {
        return new ReflectDatumWriter<>(MonitorFilingMessage.class);
    }
}
