package com.covas.model;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;

public record VideoClip(Path videoFile, Duration duration, Resolution resolution, String sourceUrl) {
    public VideoClip {
        Objects.requireNonNull(videoFile, "Caminho do vídeo não pode ser nulo");
        Objects.requireNonNull(duration, "Duração não pode ser nula");
        Objects.requireNonNull(resolution, "Resolução não pode ser nula");

        if (duration.isNegative() || duration.isZero()) {
            throw new IllegalArgumentException("Duração do vídeo tem de ser estritamente positiva");
        }
    }

    public boolean isSuitableForDuration(Duration required) {
        Objects.requireNonNull(required);
        return duration.compareTo(required) >= 0;
    }
}