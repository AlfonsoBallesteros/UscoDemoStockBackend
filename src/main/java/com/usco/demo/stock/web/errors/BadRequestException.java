package com.usco.demo.stock.web.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import java.io.Serial;
import java.net.URI;

@SuppressWarnings("java:S110") // Inheritance tree of classes should not be too deep
public class BadRequestException extends ErrorResponseException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String entityName;

    private final String errorKey;

    public BadRequestException(HttpStatus status, String defaultMessage, String entityName, String errorKey) {
        this(ErrorConstants.DEFAULT_TYPE, status, defaultMessage, entityName, errorKey);
    }

    public BadRequestException(URI type, HttpStatus status, String defaultMessage, String entityName, String errorKey) {
        super(
            status,
            ProblemDetailWithCause.ProblemDetailWithCauseBuilder
                .instance()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(type)
                .withTitle(defaultMessage)
                .withProperty("message", "error." + errorKey)
                .withProperty("params", entityName)
                .build(),
            null
        );
        this.entityName = entityName;
        this.errorKey = errorKey;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getErrorKey() {
        return errorKey;
    }

    public ProblemDetailWithCause getProblemDetailWithCause() {
        return (ProblemDetailWithCause) this.getBody();
    }
}
