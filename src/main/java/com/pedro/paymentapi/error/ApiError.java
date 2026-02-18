package com.pedro.paymentapi.payment.error;

import java.time.Instant;
import java.util.Map;

public class ApiError {

    private int status;
    private String error;
    private String message;
    private String path;
    private Instant timestamp;
    private Map<String, String> fields;

    public ApiError(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = Instant.now();
    }

    public ApiError(int status, String error, Map<String, String> fields, String path) {
        this.status = status;
        this.error = error;
        this.fields = fields;
        this.path = path;
        this.timestamp = Instant.now();
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getFields() {
        return fields;
    }
}
