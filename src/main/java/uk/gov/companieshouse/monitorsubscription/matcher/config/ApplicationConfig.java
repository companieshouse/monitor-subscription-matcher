package uk.gov.companieshouse.monitorsubscription.matcher.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;
import uk.gov.companieshouse.kafka.serialization.SerializerFactory;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.monitorsubscription.matcher.schema.MonitorFiling;
import uk.gov.companieshouse.monitorsubscription.matcher.serdes.GenericAvroDeserializer;
import uk.gov.companieshouse.monitorsubscription.matcher.serdes.GenericAvroSerializer;


@Configuration
public class ApplicationConfig implements WebMvcConfigurer {

    public static final String NAMESPACE = "monitor-subscription-matcher";

    @Bean
    public Logger logger(@Value("${spring.application.name}") String applicationName) {
        return LoggerFactory.getLogger(applicationName);
    }

    @Bean
    SerializerFactory serializerFactory() {
        return new SerializerFactory();
    }

    @Bean
    GenericAvroDeserializer<MonitorFiling> monitorFilingAvroDeserializer() {
        return new GenericAvroDeserializer<>(MonitorFiling.class);
    }

    @Bean
    GenericAvroSerializer monitorFilingAvroSerializer() {
        return new GenericAvroSerializer();
    }

    @Bean
    EnvironmentReader environmentReader() {
        return new EnvironmentReaderImpl();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(new JavaTimeModule());
    }

}
