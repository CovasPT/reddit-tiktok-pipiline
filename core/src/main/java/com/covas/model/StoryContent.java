package com.covas.model;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record StoryContent(
    String id, 
    String title, 
    String subreddit, 
    String author, 
    int score, 
    List<String> topComments, 
    Instant fetchedAt
) {
    public StoryContent {
        Objects.requireNonNull(id, "ID não pode ser nulo");
        Objects.requireNonNull(fetchedAt, "Timestamp fetchedAt não pode ser nulo");
        
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Título não pode ser nulo ou vazio");
        }
        if (subreddit == null || subreddit.isBlank()) {
            throw new IllegalArgumentException("Subreddit não pode ser nulo ou vazio");
        }
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Autor não pode ser nulo ou vazio");
        }
        
        if (score < 0) {
            throw new IllegalArgumentException("Score deve ser maior ou igual a zero");
        }

        topComments = List.copyOf(Objects.requireNonNull(topComments, "Lista de comentários não pode ser nula"));
    }

    public String fullNarrationText() {
        if (topComments.isEmpty()) {
            return title;
        }
        return title + "\n\n" + String.join("\n\n", topComments);
    }

    public int segmentCount() {
        return 1 + topComments.size();
    }
}