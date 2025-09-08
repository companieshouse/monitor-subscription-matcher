package uk.gov.companieshouse.monitorsubscription.matcher.config;

import static java.lang.String.format;

import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.RetryTopicConfiguration;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationBuilder;
import org.springframework.kafka.support.serializer.DelegatingByTypeSerializer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import uk.gov.companieshouse.delta.ChsDelta;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.monitorsubscription.matcher.config.properties.KafkaConsumerFilingProperties;
import uk.gov.companieshouse.monitorsubscription.matcher.consumer.MonitorFilingMessage;
import uk.gov.companieshouse.monitorsubscription.matcher.exception.RetryableException;
import uk.gov.companieshouse.monitorsubscription.matcher.kafka.InvalidMessageRouter;
import uk.gov.companieshouse.monitorsubscription.matcher.kafka.MessageFlags;
import uk.gov.companieshouse.monitorsubscription.matcher.serdes.MonitorFilingMessageDeserialiser;
import uk.gov.companieshouse.monitorsubscription.matcher.serdes.MonitorFilingMessageSerialiser;

@Configuration
@EnableKafka
public class KafkaConfig {

    private final Logger logger;

    public KafkaConfig(final Logger logger) {
        this.logger = logger;
    }

    @Bean(name = "filingConsumerFactory")
    public ConsumerFactory<String, MonitorFilingMessage> filingConsumerFactory(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        logger.trace(format("consumerFactory(bootstrapServers=%s) method called.", bootstrapServers));

        return new DefaultKafkaConsumerFactory<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class,
                        ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class,
                        ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, MonitorFilingMessageDeserialiser.class,
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                        ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"),
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(new MonitorFilingMessageDeserialiser()));
    }

    @Bean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, MonitorFilingMessage> kafkaListenerContainerFactory(
            ConsumerFactory<String, MonitorFilingMessage> consumerFactory,
            KafkaConsumerFilingProperties filingProperties) {
        logger.trace("kafkaListenerContainerFactory() method called.");

        ConcurrentKafkaListenerContainerFactory<String, MonitorFilingMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(filingProperties.getConcurrency());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
    /*
    @Bean
    public ProducerFactory<String, Object> producerFactory(MessageFlags messageFlags,
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
            @Value("${consumer.filing.topic}") String topic,
            @Value("${consumer.filing.group-id}") String groupId,
            @Value("${spring.application.name}") String applicationName) {
        logger.trace("producerFactory() method called.");

        return new DefaultKafkaProducerFactory<>(
                Map.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                        ProducerConfig.ACKS_CONFIG, "all",
                        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DelegatingByTypeSerializer.class,
                        ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, InvalidMessageRouter.class.getName(),
                        "message-flags", messageFlags,
                        "invalid-topic", "%s-%s-invalid".formatted(topic, groupId),
                        "application-name", applicationName),
                new StringSerializer(),
                new DelegatingByTypeSerializer(
                        Map.of(
                                byte[].class, new ByteArraySerializer(),
                                ChsDelta.class, new MonitorFilingMessageSerialiser())));
    }
    */
    @Bean
    public ProducerFactory<String, Object> producerFactory(MessageFlags messageFlags,
            KafkaConsumerFilingProperties filingProperties,
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
            @Value("${spring.application.name}") String applicationName) {
        logger.trace("producerFactory() method called.");

        return new DefaultKafkaProducerFactory<>(
                Map.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                        ProducerConfig.ACKS_CONFIG, "all",
                        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DelegatingByTypeSerializer.class,
                        ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, InvalidMessageRouter.class.getName(),
                        "message-flags", messageFlags,
                        "invalid-topic", "%s-%s-invalid".formatted(filingProperties.getTopic(), filingProperties.getGroupId()),
                        "application-name", applicationName),
                new StringSerializer(),
                new DelegatingByTypeSerializer(
                        Map.of(
                                byte[].class, new ByteArraySerializer(),
                                ChsDelta.class, new MonitorFilingMessageSerialiser())));
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        logger.trace("kafkaTemplate() method called.");

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean(name = "filingRetryTopicConfiguration")
    public RetryTopicConfiguration filingRetryTopicConfiguration(KafkaTemplate<String, Object> template, KafkaConsumerFilingProperties properties) {
        logger.trace("retryTopicConfiguration() method called.");

        return RetryTopicConfigurationBuilder
                .newInstance()
                .doNotAutoCreateRetryTopics() // this is necessary to prevent failing connection during loading of spring app context
                .maxAttempts(properties.getMaxAttempts())
                .fixedBackOff(properties.getBackOffDelay())
                .useSingleTopicForSameIntervals()
                .retryTopicSuffix("-%s-retry".formatted(properties.getGroupId()))
                .dltSuffix("-%s-error".formatted(properties.getGroupId()))
                .dltProcessingFailureStrategy(DltStrategy.FAIL_ON_ERROR)
                .retryOn(RetryableException.class)
                .create(template);
    }
}
