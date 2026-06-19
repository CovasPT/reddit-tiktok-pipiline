package com.covas.pipeline;

import com.covas.model.*;
import com.covas.port.*;
import com.covas.exception.*;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PipelineOrchestrator {
    private final StoryFetcher fetcher;
    private final TtsStrategy tts;
    private final FrameRenderer renderer;
    private final BackgroundVideoProvider videoProvider;
    private final VideoCompiler compiler;
    private final TtsOptions defaultTtsOptions;
    private final RetryPolicy retryPolicy;

    public PipelineOrchestrator(StoryFetcher fetcher, TtsStrategy tts, FrameRenderer renderer,
                                BackgroundVideoProvider videoProvider, VideoCompiler compiler,
                                TtsOptions defaultTtsOptions) {
        this.fetcher = fetcher;
        this.tts = tts;
        this.renderer = renderer;
        this.videoProvider = videoProvider;
        this.compiler = compiler;
        this.defaultTtsOptions = defaultTtsOptions;
        this.retryPolicy = RetryPolicy.withDefaults();
    }

  public List<PipelineResult> run(String subreddit, int limit, Path outputDir) {
        List<PipelineResult> results = new ArrayList<>();
        
        try {
            List<StoryContent> stories = fetcher.fetchTopStories(subreddit, limit);
            
            for (StoryContent story : stories) {
                results.add(processStory(story, outputDir));
            }
        } catch (PipelineException e) {
            // Apanha o erro do RedditStoryFetcher de forma controlada
            results.add(new PipelineResult.Failure(e.stageId(), e.getMessage(), e));
        } catch (Exception e) {
            results.add(new PipelineResult.Failure("orchestrator-init", "Erro inesperado ao iniciar run: " + e.getMessage(), e));
        }

        return results;
    }

    private PipelineResult processStory(StoryContent story, Path outputDir) {
        String runId = UUID.randomUUID().toString();
        Instant startedAt = Instant.now();
        
        try {
            AudioResult audio = retryPolicy.execute(() -> tts.synthesize(story.fullNarrationText(), defaultTtsOptions));
            FrameAssets frames = renderer.render(story, Resolution.TIKTOK_VERTICAL);
            VideoClip background = videoProvider.provide(audio.estimatedDuration());
            
            Path outputPath = outputDir.resolve("video_" + runId + ".mp4");
            Path finalVideo = compiler.compile(background, audio, frames, outputPath);
            
            Duration processingTime = Duration.between(startedAt, Instant.now());
            return new PipelineResult.Success(finalVideo, story, processingTime, Instant.now());
            
        } catch (PipelineException e) {
            return new PipelineResult.Failure(e.stageId(), e.getMessage(), e);
        } catch (Exception e) {
            return new PipelineResult.Failure("orchestrator", "Erro inesperado: " + e.getMessage(), e);
        }
    }

    public void printSummary(PipelineResult result) {
        switch (result) {
            case PipelineResult.Success s -> System.out.println("Sucesso! Vídeo gerado em: " + s.outputVideo() + " (Tempo: " + s.processingTime().toSeconds() + "s)");
            case PipelineResult.Failure f -> System.out.println("Falhou no estágio [" + f.stageId() + "]: " + f.reason());
        }
    }
}