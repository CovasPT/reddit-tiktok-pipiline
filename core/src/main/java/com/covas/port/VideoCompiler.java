package com.covas.port;

import com.covas.model.AudioResult;
import com.covas.model.FrameAssets;
import com.covas.model.VideoClip;
import java.nio.file.Path;

public interface VideoCompiler {
    
    /**
     * Compila todos os elementos visuais e sonoros num único ficheiro de vídeo.
     */
    Path compile(VideoClip bg, AudioResult audio, FrameAssets frames, Path out);

}