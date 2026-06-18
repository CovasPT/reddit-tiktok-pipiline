package com.covas.model;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public sealed interface PipelineResult permits PipelineResult.Success, PipelineResult.Failure {

    record Success(Path outputVideo, StoryContent source, Duration processingTime, Instant completedAt) implements PipelineResult {
        public Success {
            Objects.requireNonNull(outputVideo, "Output video não pode ser nulo");
            Objects.requireNonNull(source, "Source não pode ser nulo");
            Objects.requireNonNull(processingTime, "Processing time não pode ser nulo");
            Objects.requireNonNull(completedAt, "Completed at não pode ser nulo");
        }
    }

    record Failure(String stageId, String reason, Throwable cause) implements PipelineResult {
        public Failure {
            Objects.requireNonNull(stageId, "StageId não pode ser nulo");
            Objects.requireNonNull(reason, "Reason não pode ser nulo");
        }

        public Failure(String stageId, String reason) {
            this(stageId, reason, null);
        }

        public boolean hasCause() {
            return cause != null;
        }
    }
}