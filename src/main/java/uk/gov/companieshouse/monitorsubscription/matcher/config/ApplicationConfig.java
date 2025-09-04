package uk.gov.companieshouse.monitorsubscription.matcher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Configuration
public class ApplicationConfig {

    public static final String NAMESPACE = "monitor-subscription-matcher";

    @Bean
    public Logger logger(@Value("${spring.application.name}") String applicationName) {
        return LoggerFactory.getLogger(applicationName);
    }

}
