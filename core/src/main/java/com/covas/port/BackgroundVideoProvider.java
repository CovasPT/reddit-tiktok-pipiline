package com.covas.port;

import com.covas.model.VideoClip;
import java.time.Duration;

public interface BackgroundVideoProvider {
    /**
     * Fornece um clip de vídeo que tenha pelo menos a duração mínima exigida.
     */
    VideoClip provide(Duration minimumDuration);
}