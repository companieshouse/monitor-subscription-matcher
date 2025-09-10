package uk.gov.companieshouse.monitorsubscription.matcher.serdes;

import consumer.exception.NonRetryableErrorException;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.kafka.common.serialization.Deserializer;

//@Component
public class GenericAvroDeserializer<T> implements Deserializer<T> {

    Class<T> avroClass;

    public GenericAvroDeserializer(Class<T> avroClass) {
        this.avroClass = avroClass;
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            DatumReader<T> reader = new ReflectDatumReader<>(avroClass);
            T record = reader.read(null, decoder);
            return record;
        } catch (Exception ex) {
            throw new NonRetryableErrorException(ex);
        }
    }
}