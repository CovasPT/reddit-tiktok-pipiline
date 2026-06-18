package com.covas.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public record FrameAssets(Path titleScreenshot, List<Path> commentScreenshots, Resolution resolution) {
    public FrameAssets {
        Objects.requireNonNull(titleScreenshot, "Screenshot do título não pode ser nulo");
        Objects.requireNonNull(resolution, "Resolução não pode ser nula");
        
        commentScreenshots = List.copyOf(Objects.requireNonNull(commentScreenshots, "Lista de comentários não nula"));
    }

    public List<Path> allScreenshots() {
        return Stream.concat(Stream.of(titleScreenshot), commentScreenshots.stream()).toList();
    }

    public int totalFrames() {
        return 1 + commentScreenshots.size();
    }
}