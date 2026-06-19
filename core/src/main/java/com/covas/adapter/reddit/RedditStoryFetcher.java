package com.covas.adapter.reddit;

import com.covas.model.StoryContent;
import com.covas.port.StoryFetcher;
import com.covas.exception.FetchException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class RedditStoryFetcher implements StoryFetcher {

    private static final String BASE_URL = "https://arctic-shift.photon-reddit.com/api/posts/search";

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

      @Override
    public List<StoryContent> fetchTopStories(String subreddit, int limit) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
.uri(URI.create(BASE_URL + "?subreddit=" + subreddit + "&limit=" + limit + "&sort=desc"))

                    .GET()
                    .build();

            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200) {
                throw new FetchException("Reddit devolveu " + res.statusCode(), null);
            }

            JsonNode children = mapper.readTree(res.body()).path("data");
            List<StoryContent> stories = new ArrayList<>();

            for (JsonNode child : children) {
                JsonNode post = child;
                String body = post.path("selftext").asText("").strip();
                stories.add(new StoryContent(
                        post.path("id").asText(),
                        post.path("title").asText(),
                        subreddit,
                        post.path("author").asText(),
                        post.path("score").asInt(),
                        List.of(body.isBlank() ? "(post sem texto)" : body),
                        Instant.now()
                ));
            }
            return stories;

        } catch (FetchException e) {
            throw e;
        } catch (Exception e) {
            throw new FetchException("Falha ao comunicar com o Reddit: " + e.getMessage(), e);
        }
    }
}
