package com.covas.exception;

public class TtsException extends PipelineException {
    private final int httpStatusCode;

    public TtsException(String message, int httpStatusCode) {
        super("tts", message);
        this.httpStatusCode = httpStatusCode;
    }

    public int httpStatusCode() {
        return httpStatusCode;
    }

    public boolean isRetryable() {
        return httpStatusCode == 429 || httpStatusCode == 503;
    }
}