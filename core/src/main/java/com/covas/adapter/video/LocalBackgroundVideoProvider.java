package com.covas.adapter.video;

import com.covas.model.Resolution;
import com.covas.model.VideoClip;
import com.covas.port.BackgroundVideoProvider;
import com.covas.exception.VideoProviderException;

import java.nio.file.Path;
import java.time.Duration;

public class LocalBackgroundVideoProvider implements BackgroundVideoProvider {

    @Override
    public VideoClip provide(Duration minimumDuration) {
        try {
            // TODO: Lógica real para ler uma pasta e escolher um ficheiro aleatório

            VideoClip clip = new VideoClip(
                    Path.of("backgrounds/minecraft_parkour.mp4"),
                    Duration.ofMinutes(10), // Duração total fictícia
                    Resolution.TIKTOK_VERTICAL,
                    "local"
            );

            if (!clip.isSuitableForDuration(minimumDuration)) {
                throw new VideoProviderException("O vídeo selecionado é demasiado curto para o áudio.");
            }

            return clip;

        } catch (Exception e) {
            throw new VideoProviderException("Erro ao obter vídeo de fundo local", e);
        }
    }
}