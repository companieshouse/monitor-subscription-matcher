package uk.gov.companieshouse.monitorsubscription.matcher.exception;

public class RetryableException extends RuntimeException {

    public RetryableException(String message, Throwable cause) {
        super(message, cause);
    }
}
