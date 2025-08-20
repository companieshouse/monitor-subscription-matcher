package uk.gov.companieshouse.monitorsubscription.matcher.config;

import static uk.gov.companieshouse.monitorsubscription.matcher.Application.NAMESPACE;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Configuration
public class LoggingConfig {

    @Bean
    Logger logger() {
        return LoggerFactory.getLogger(NAMESPACE);
    }

}
