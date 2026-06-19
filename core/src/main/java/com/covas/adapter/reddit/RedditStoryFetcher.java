package com.covas.adapter.reddit;

import com.covas.model.StoryContent;
import com.covas.port.StoryFetcher;
import com.covas.exception.FetchException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RedditStoryFetcher implements StoryFetcher {

    @Override
    public List<StoryContent> fetchTopStories(String subreddit, int limit) {
        try {
            String url = "https://www.reddit.com/r/" + subreddit + "/top.rss?limit=" + limit;
            
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .get();

            List<StoryContent> stories = new ArrayList<>();
            for (org.jsoup.nodes.Element item : doc.select("item")) {
                String title = item.select("title").text();
                String author = item.select("author").text();
                
                stories.add(new StoryContent(
                    "id_" + System.currentTimeMillis(),
                    title,
                    subreddit,
                    author,
                    0, 
                    List.of("Este é um comentário simulado para teste."),
                    Instant.now()
                ));
                
                if (stories.size() >= limit) break;
            }
            return stories;
        } catch (Exception e) {
            throw new FetchException("Falha ao ler o RSS do Reddit: " + e.getMessage(), e);
        }
    }
}