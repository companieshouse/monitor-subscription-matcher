package uk.gov.companieshouse.monitorsubscription.matcher.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.monitorsubscription.matcher.consumer.Data;
import uk.gov.companieshouse.monitorsubscription.matcher.consumer.DescriptionValues;
import uk.gov.companieshouse.monitorsubscription.matcher.consumer.MonitorFilingMessage;
import uk.gov.companieshouse.monitorsubscription.matcher.consumer.Payload;
import uk.gov.companieshouse.monitorsubscription.matcher.serdes.ArrayNodeDeserialiser;

@Configuration
public class SerdesConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(new JavaTimeModule());
    }

    @Bean
    public ArrayNodeDeserialiser<String> stringArrayNodeDeserialiser(ObjectMapper objectMapper) {
        return new ArrayNodeDeserialiser<>(objectMapper, String.class);
    }

    @Bean
    public ArrayNodeDeserialiser<MonitorFilingMessage> capitalArrayNodeDeserialiser(ObjectMapper objectMapper) {
        return new ArrayNodeDeserialiser<>(objectMapper, MonitorFilingMessage.class);
    }

    @Bean
    public ArrayNodeDeserialiser<Payload> altCapitalArrayNodeDeserialiser(ObjectMapper objectMapper) {
        return new ArrayNodeDeserialiser<>(objectMapper, Payload.class);
    }

    @Bean
    public ArrayNodeDeserialiser<Data> annotationArrayNodeDeserialiser(ObjectMapper objectMapper) {
        return new ArrayNodeDeserialiser<>(objectMapper, Data.class);
    }

    @Bean
    public ArrayNodeDeserialiser<DescriptionValues> resolutionArrayNodeDeserialiser(ObjectMapper objectMapper) {
        return new ArrayNodeDeserialiser<>(objectMapper, DescriptionValues.class);
    }

}
