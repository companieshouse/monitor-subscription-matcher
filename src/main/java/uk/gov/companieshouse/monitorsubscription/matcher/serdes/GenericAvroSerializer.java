package uk.gov.companieshouse.monitorsubscription.matcher.serdes;

import consumer.exception.NonRetryableErrorException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Serializer;

//@Component
public class GenericAvroSerializer implements Serializer<Object> {

    @Override
    public byte[] serialize(String topic, Object payload) {

        try {
            if (payload == null) {
                return null;
            }

            if (payload instanceof byte[]) {
                return (byte[]) payload;
            }

            if (payload instanceof SpecificRecordBase) {

                DatumWriter<SpecificRecordBase> writer = new SpecificDatumWriter<>();
                EncoderFactory encoderFactory = EncoderFactory.get();

                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    Encoder encoder = encoderFactory.binaryEncoder(out, null);
                    writer.setSchema(((SpecificRecordBase) payload).getSchema());
                    writer.write((SpecificRecordBase) payload, encoder);
                    encoder.flush();

                    byte[] serializedData = out.toByteArray();
                    encoder.flush();
                    return serializedData;
                } catch (IOException ex) {
                    throw new NonRetryableErrorException("Failed to serialize message for Kafka: " + (((SpecificRecordBase) payload).get("name")), ex);
                }
            }

            return payload.toString().getBytes(StandardCharsets.UTF_8);

        } catch (Exception ex) {
            throw new NonRetryableErrorException(ex);
        }
    }
}