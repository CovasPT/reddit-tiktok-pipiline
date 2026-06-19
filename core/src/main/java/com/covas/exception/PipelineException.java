package com.covas.exception;

import java.util.Objects;

public abstract class PipelineException extends RuntimeException {
    private final String stageId;

    protected PipelineException(String stageId, String message) {
        super(message);
        this.stageId = Objects.requireNonNull(stageId, "StageId não pode ser nulo");
    }

    protected PipelineException(String stageId, String message, Throwable cause) {
        super(message, cause);
        this.stageId = Objects.requireNonNull(stageId, "StageId não pode ser nulo");
    }

    public String stageId() {
        return stageId;
    }
}