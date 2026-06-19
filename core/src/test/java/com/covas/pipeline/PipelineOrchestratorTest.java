package com.covas.pipeline;

import com.covas.exception.FetchException;
import com.covas.exception.TtsException;
import com.covas.model.*;
import com.covas.port.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PipelineOrchestratorTest {

    @Mock StoryFetcher fetcher;
    @Mock TtsStrategy tts;
    @Mock FrameRenderer renderer;
    @Mock BackgroundVideoProvider videoProvider;
    @Mock VideoCompiler compiler;

    private PipelineOrchestrator orchestrator;
    private final Path outputDir = Path.of("target/test-output");

    @BeforeEach
    void setUp() {
        TtsOptions opts = TtsOptions.builder().voiceId("v1").modelId("m1").build();
        orchestrator = new PipelineOrchestrator(fetcher, tts, renderer, videoProvider, compiler, opts);
    }

    @Test
    void returnsFetchFailureWhenFetcherThrows() {
        when(fetcher.fetchTopStories(anyString(), anyInt()))
                .thenThrow(new FetchException("403 Reddit", null));

        List<PipelineResult> results = orchestrator.run("programacao", 1, outputDir);

        assertEquals(1, results.size());
        assertInstanceOf(PipelineResult.Failure.class, results.get(0));
        PipelineResult.Failure failure = (PipelineResult.Failure) results.get(0);
        assertEquals("reddit-fetch", failure.stageId());
    }

    @Test
    void returnsEmptyListWhenNoStoriesFound() {
        when(fetcher.fetchTopStories(anyString(), anyInt())).thenReturn(List.of());

        List<PipelineResult> results = orchestrator.run("programacao", 1, outputDir);

        assertTrue(results.isEmpty());
        verifyNoInteractions(tts, renderer, videoProvider, compiler);
    }

    @Test
    void returnsTtsFailureWhenTtsThrows() {
        StoryContent story = new StoryContent("id1", "Título", "programacao", "autor", 10, List.of(), Instant.now());
        when(fetcher.fetchTopStories(anyString(), anyInt())).thenReturn(List.of(story));
        when(tts.synthesize(anyString(), any())).thenThrow(new TtsException("API falhou", 500));

        List<PipelineResult> results = orchestrator.run("programacao", 1, outputDir);

        assertEquals(1, results.size());
        assertInstanceOf(PipelineResult.Failure.class, results.get(0));
        assertEquals("tts", ((PipelineResult.Failure) results.get(0)).stageId());
    }

    @Test
    void processesMultipleStoriesIndependently() {
        StoryContent s1 = new StoryContent("id1", "Título 1", "programacao", "autor1", 10, List.of(), Instant.now());
        StoryContent s2 = new StoryContent("id2", "Título 2", "programacao", "autor2", 20, List.of(), Instant.now());
        when(fetcher.fetchTopStories(anyString(), anyInt())).thenReturn(List.of(s1, s2));
        when(tts.synthesize(anyString(), any())).thenThrow(new TtsException("falhou", 500));

        List<PipelineResult> results = orchestrator.run("programacao", 2, outputDir);

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(r -> r instanceof PipelineResult.Failure));
    }

    @Test
    void throwsNullPointerWhenFetcherReturnsNull() {
        when(fetcher.fetchTopStories(anyString(), anyInt())).thenReturn(null);

        List<PipelineResult> results = orchestrator.run("programacao", 1, outputDir);

        assertEquals(1, results.size());
        assertInstanceOf(PipelineResult.Failure.class, results.get(0));
    }
}
