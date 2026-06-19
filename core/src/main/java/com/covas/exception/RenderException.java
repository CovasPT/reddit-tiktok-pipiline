package com.covas.exception;

public class RenderException extends PipelineException {
    public RenderException(String message) {
        super("renderer", message);
    }

    public RenderException(String message, Throwable cause) {
        super("renderer", message, cause);
    }
}