package com.covas.exception;

public class VideoProviderException extends PipelineException {
    public VideoProviderException(String message) {
        super("downloader", message);
    }

    public VideoProviderException(String message, Throwable cause) {
        super("downloader", message, cause);
    }
}