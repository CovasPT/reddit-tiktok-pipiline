package com.covas.adapter.video;

import com.covas.model.Resolution;
import com.covas.model.VideoClip;
import com.covas.port.BackgroundVideoProvider;
import com.covas.exception.VideoProviderException;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;


public class YoutubeVideoProvider implements BackgroundVideoProvider {

    private final List<String> videoPool = List.of(
        "https://www.youtube.com/watch?v=n_Dv4JMiwK8", // Exemplo: GTA Parkour
        "https://www.youtube.com/watch?v=EX0v0k5I47E"  // Exemplo: Minecraft Long Gameplay
    );

    @Override
    public VideoClip provide(Duration minimumDuration) {
        try {
            // Garante que a pasta existe
            Path bgFolder = Path.of("backgrounds");
            if (!Files.exists(bgFolder)) {
                Files.createDirectories(bgFolder);
            }

            String videoUrl = videoPool.get(ThreadLocalRandom.current().nextInt(videoPool.size()));
            Path outputPath = bgFolder.resolve("bg_" + UUID.randomUUID() + ".mp4");

            ProcessBuilder pb = new ProcessBuilder(
                    "yt-dlp", 
                    "-f", "mp4", 
                    "-o", outputPath.toString(), 
                    videoUrl
            );
            
            pb.redirectErrorStream(true); // Redireciona o output para conseguirmos ver se falhar
            
            System.out.println("A descarregar vídeo de fundo via yt-dlp... Isto pode demorar.");
            int exitCode;
            try {
                exitCode = pb.start().waitFor();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new VideoProviderException("Download interrompido", e);
            }

            if (exitCode != 0) {
                throw new VideoProviderException("O yt-dlp falhou com código: " + exitCode + ". Tens o yt-dlp instalado no teu PC?");
            }

            // Assumimos 30 minutos (idealmente ler com ffprobe)
            VideoClip clip = new VideoClip(outputPath, Duration.ofMinutes(30), Resolution.TIKTOK_VERTICAL, "youtube");

            if (!clip.isSuitableForDuration(minimumDuration)) {
                throw new VideoProviderException("O vídeo descarregado é demasiado curto para a narração.");
            }

            return clip;

        } catch (VideoProviderException e) {
            throw e;
        } catch (Exception e) {
            throw new VideoProviderException("Erro ao descarregar vídeo do YouTube", e);
        }
    }
}