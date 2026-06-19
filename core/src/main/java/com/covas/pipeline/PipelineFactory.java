package com.covas.pipeline;

import com.covas.adapter.ffmpeg.FfmpegVideoCompiler;
import com.covas.adapter.reddit.RedditStoryFetcher;
import com.covas.adapter.renderer.HtmlFrameRenderer;
import com.covas.adapter.tts.ElevenLabsTtsStrategy;
import com.covas.adapter.video.LocalBackgroundVideoProvider;
import com.covas.model.TtsOptions;
import com.microsoft.playwright.Browser;

public final class PipelineFactory {

    private PipelineFactory() {}

    public static PipelineOrchestrator create(Browser browser) {
        TtsOptions ttsOptions = TtsOptions.builder()
                .voiceId(envOrDefault("ELEVENLABS_VOICE_ID", "21m00Tcm4TlvDq8ikWAM"))
                .modelId(envOrDefault("ELEVENLABS_MODEL_ID", "eleven_multilingual_v2"))
                .build();

        return new PipelineOrchestrator(
                new RedditStoryFetcher(),
                new ElevenLabsTtsStrategy(),
                new HtmlFrameRenderer(browser),
                new LocalBackgroundVideoProvider(),
                new FfmpegVideoCompiler(),
                ttsOptions
        );
    }

    private static String envOrDefault(String key, String fallback) {
        String value = System.getenv(key);
        return (value != null && !value.isBlank()) ? value : fallback;
    }
}
