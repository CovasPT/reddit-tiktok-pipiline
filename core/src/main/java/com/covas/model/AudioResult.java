package com.covas.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;

public record AudioResult(Path audioFile, Duration estimatedDuration, String providerUsed) {
    public AudioResult {
        Objects.requireNonNull(audioFile, "Caminho do ficheiro de áudio não pode ser nulo");
        Objects.requireNonNull(estimatedDuration, "Duração estimada não pode ser nula");
        Objects.requireNonNull(providerUsed, "Provider não pode ser nulo");

        if (!Files.exists(audioFile)) {
            throw new IllegalArgumentException("Ficheiro de áudio não encontrado em disco: " + audioFile);
        }

        if (estimatedDuration.isNegative()) {
            throw new IllegalArgumentException("Duração não pode ser negativa");
        }
    }
}