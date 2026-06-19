package com.covas.exception;

public class CompilationException extends PipelineException {
    private final String ffmpegOutput;

    public CompilationException(String message, String ffmpegOutput) {
        super("compiler", message);
        this.ffmpegOutput = ffmpegOutput;
    }

    public CompilationException(String message, String ffmpegOutput, Throwable cause) {
        super("compiler", message, cause);
        this.ffmpegOutput = ffmpegOutput;
    }

    public String ffmpegOutput() {
        return ffmpegOutput;
    }
}