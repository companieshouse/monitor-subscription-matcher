package uk.gov.companieshouse.monitorsubscription.matcher.config;

import java.util.HashMap;
import java.util.Map;
import monitor.transaction;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.monitorsubscription.matcher.config.properties.MonitorFilingConsumerProperties;
import uk.gov.companieshouse.monitorsubscription.matcher.config.properties.NotificationMatchProducerProperties;
import uk.gov.companieshouse.monitorsubscription.matcher.exception.RetryableTopicErrorInterceptor;
import uk.gov.companieshouse.monitorsubscription.matcher.serdes.GenericSerializer;
import uk.gov.companieshouse.monitorsubscription.matcher.serdes.MonitorFilingDeserializer;

@Configuration
@EnableKafka
@Profile("!test")
public class KafkaConfig {

    private final MonitorFilingConsumerProperties monitorFilingProperties;
    private final NotificationMatchProducerProperties notificationMatchProperties;
    private final String bootstrapServers;
    private final Logger logger;

    /**
     * Constructor.
     */
    public KafkaConfig(MonitorFilingConsumerProperties monitorFilingProperties,
            NotificationMatchProducerProperties notificationMatchProperties,
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
            Logger logger) {
        this.monitorFilingProperties = monitorFilingProperties;
        this.notificationMatchProperties = notificationMatchProperties;
        this.bootstrapServers = bootstrapServers;
        this.logger = logger;
    }

    /**
     * Kafka MonitorFilingConsumer Factory.
     */
    @Bean(name = "kafkaConsumerFactory")
    public ConsumerFactory<String, transaction> kafkaConsumerFactory() {
        logger.trace("kafkaConsumerFactory() method called.");

        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, MonitorFilingDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new ErrorHandlingDeserializer<>(new MonitorFilingDeserializer()));
    }

    /**
     * Kafka MonitorFiling Producer Factory.
     */
    @Bean(name = "kafkaProducerFactory")
    public ProducerFactory<String, Object> kafkaProducerFactory() {
        logger.trace("kafkaProducerFactory() method called.");

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "false");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GenericSerializer.class);
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, RetryableTopicErrorInterceptor.class.getName());

        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), new GenericSerializer());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        logger.trace("kafkaTemplate() method called.");

        return new KafkaTemplate<>(kafkaProducerFactory());
    }

    /**
     * Kafka Listener Container Factory.
     */
    @Bean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, transaction> kafkaListenerContainerFactory() {
        logger.trace("kafkaListenerContainerFactory() method called.");

        ConcurrentKafkaListenerContainerFactory<String, transaction> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaConsumerFactory());
        factory.setConcurrency(monitorFilingProperties.getConcurrency());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        return factory;
    }

}
