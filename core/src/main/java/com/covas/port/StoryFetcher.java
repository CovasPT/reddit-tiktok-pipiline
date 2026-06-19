package com.covas.port;

import com.covas.model.StoryContent;
import java.util.List;

public interface StoryFetcher {
    /**
     * Vai buscar as histórias de topo de um determinado subreddit.
     */
    List<StoryContent> fetchTopStories(String subreddit, int limit);
}