package com.covas.adapter.tts;

import com.covas.model.AudioResult;
import com.covas.model.TtsOptions;
import com.covas.port.TtsStrategy;
import com.covas.exception.TtsException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

public final class ElevenLabsTtsStrategy implements TtsStrategy {

    private static final String API_BASE = "https://api.elevenlabs.io/v1/text-to-speech";
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public AudioResult synthesize(String text, TtsOptions opts) {
        String apiKey = System.getenv("ELEVENLABS_API_KEY");
        try {
            Path audioFile = (apiKey != null && !apiKey.isBlank())
                    ? callApi(text, opts, apiKey)
                    : generateSilence(estimateDuration(text));
            return new AudioResult(audioFile, Duration.ofSeconds(estimateDuration(text)), providerId());
        } catch (TtsException e) {
            throw e;
        } catch (Exception e) {
            throw new TtsException("Erro no TTS: " + e.getMessage(), 500);
        }
    }

    private Path callApi(String text, TtsOptions opts, String apiKey) throws Exception {
        String body = mapper.writeValueAsString(Map.of(
                "text", text,
                "model_id", opts.modelId(),
                "voice_settings", Map.of(
                        "stability", opts.stability(),
                        "similarity_boost", opts.similarityBoost(),
                        "style", opts.style()
                )
        ));

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE + "/" + opts.voiceId()))
                .header("xi-api-key", apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<byte[]> res = http.send(req, HttpResponse.BodyHandlers.ofByteArray());
        if (res.statusCode() != 200) {
            throw new TtsException("ElevenLabs devolveu " + res.statusCode(), res.statusCode());
        }

        Path audioFile = Files.createTempFile("narration_", ".mp3");
        Files.write(audioFile, res.body());
        return audioFile;
    }

    private Path generateSilence(long seconds) throws Exception {
        Path audioFile = Files.createTempFile("narration_silence_", ".mp3");
        int exitCode = new ProcessBuilder(
                "ffmpeg", "-y",
                "-f", "lavfi", "-i", "anullsrc=r=44100:cl=stereo",
                "-t", String.valueOf(seconds),
                "-acodec", "libmp3lame", "-q:a", "9",
                audioFile.toString()
        )
        .redirectOutput(ProcessBuilder.Redirect.DISCARD)
        .redirectError(ProcessBuilder.Redirect.DISCARD)
        .start().waitFor();

        if (exitCode != 0) throw new TtsException("Falha ao gerar silêncio com FFmpeg", 500);
        return audioFile;
    }

    private long estimateDuration(String text) {
        long words = text.split("\\s+").length;
        return Math.max(5, (words * 60) / 150);
    }

    @Override
    public String providerId() {
        return "elevenlabs";
    }
}
