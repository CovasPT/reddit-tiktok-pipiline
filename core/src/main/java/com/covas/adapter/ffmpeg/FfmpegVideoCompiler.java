package com.covas.adapter.ffmpeg;

import com.covas.model.AudioResult;
import com.covas.model.FrameAssets;
import com.covas.model.VideoClip;
import com.covas.port.VideoCompiler;
import com.covas.exception.CompilationException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FfmpegVideoCompiler implements VideoCompiler {
    @Override
    public Path compile(VideoClip bg, AudioResult audio, FrameAssets frames, Path out) {
        try {
            List<String> args = new ArrayList<>(List.of("ffmpeg", "-y", "-i", bg.videoFile().toString(), "-i", audio.audioFile().toString()));
            
            args.add("-i"); args.add(frames.titleScreenshot().toString());
            for (Path p : frames.commentScreenshots()) {
                args.add("-i"); args.add(p.toString());
            }

            args.addAll(List.of("-c:v", "libx264", "-c:a", "aac", "-shortest", out.toString()));
            
            int exitCode = new ProcessBuilder(args).redirectErrorStream(true).start().waitFor();
            if (exitCode != 0) throw new CompilationException("FFmpeg falhou", "Código: " + exitCode);
            
            return out;
        } catch (Exception e) {
            throw new CompilationException("Erro na compilação", "Sem output", e);
        }
    }
}