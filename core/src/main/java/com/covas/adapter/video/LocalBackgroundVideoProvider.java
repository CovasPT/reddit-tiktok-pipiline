package com.covas.adapter.video;

import com.covas.model.Resolution;
import com.covas.model.VideoClip;
import com.covas.port.BackgroundVideoProvider;
import com.covas.exception.VideoProviderException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class LocalBackgroundVideoProvider implements BackgroundVideoProvider {

    @Override
    public VideoClip provide(Duration minimumDuration) {
        try {
            Path bgFolder = Path.of("backgrounds");

            if (!Files.exists(bgFolder)) {
                throw new VideoProviderException("Pasta 'backgrounds/' não encontrada.");
            }

            List<Path> videoFiles;
            try (var stream = Files.list(bgFolder)) {
                videoFiles = stream
                        .filter(p -> p.toString().toLowerCase().endsWith(".mp4"))
                        .collect(Collectors.toList());
            }

            if (videoFiles.isEmpty()) {
                throw new VideoProviderException("Nenhum ficheiro .mp4 encontrado em 'backgrounds/'.");
            }

            Path chosen = videoFiles.get(ThreadLocalRandom.current().nextInt(videoFiles.size()));

            VideoClip clip = new VideoClip(chosen, Duration.ofMinutes(10), Resolution.TIKTOK_VERTICAL, "local");

            if (!clip.isSuitableForDuration(minimumDuration)) {
                throw new VideoProviderException("O vídeo '" + chosen.getFileName() + "' é demasiado curto para o áudio.");
            }

            return clip;

        } catch (VideoProviderException e) {
            throw e;
        } catch (Exception e) {
            throw new VideoProviderException("Erro ao obter vídeo de fundo local", e);
        }
    }
}