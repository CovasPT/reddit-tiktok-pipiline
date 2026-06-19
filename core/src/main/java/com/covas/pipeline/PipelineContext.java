package com.covas.pipeline;

import com.covas.model.StoryContent;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;

public record PipelineContext(String runId, StoryContent story, Path workDir, Instant startedAt) {
    public PipelineContext {
        Objects.requireNonNull(runId, "RunId não pode ser nulo");
        Objects.requireNonNull(story, "Story não pode ser nulo");
        Objects.requireNonNull(workDir, "WorkDir não pode ser nulo");
        Objects.requireNonNull(startedAt, "StartedAt não pode ser nulo");
    }
}