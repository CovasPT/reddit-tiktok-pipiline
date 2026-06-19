package com.covas.exception;

public class FetchException extends PipelineException {
    
    public FetchException(String message) {
        super("reddit-fetch", message);
    }

    public FetchException(String message, Throwable cause) {
        super("reddit-fetch", message, cause);
    }
}