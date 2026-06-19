package com.covas.adapter.ffmpeg;

import com.covas.model.AudioResult;
import com.covas.model.FrameAssets;
import com.covas.model.VideoClip;
import com.covas.port.VideoCompiler;
import com.covas.exception.CompilationException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class FfmpegVideoCompiler implements VideoCompiler {

    @Override
    public Path compile(VideoClip bg, AudioResult audio, FrameAssets frames, Path out) {
        try {
            Files.createDirectories(out.getParent());

            List<String> cmd = new ArrayList<>();
            cmd.addAll(List.of("ffmpeg", "-y",
                    "-i", bg.videoFile().toString(),
                    "-i", audio.audioFile().toString()));

            for (Path screenshot : frames.allScreenshots()) {
                cmd.addAll(List.of("-i", screenshot.toString()));
            }

            cmd.addAll(List.of(
                    "-filter_complex", buildFilterComplex(frames, audio),
                    "-map", "[out]", "-map", "1:a",
                    "-c:v", "libx264", "-c:a", "aac",
                    "-pix_fmt", "yuv420p", "-shortest",
                    out.toString()));

            int exitCode = new ProcessBuilder(cmd)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start().waitFor();

            if (exitCode != 0) {
                throw new CompilationException("FFmpeg falhou com código " + exitCode, String.valueOf(exitCode));
            }
            return out;

        } catch (CompilationException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CompilationException("Processo FFmpeg interrompido", "", e);
        } catch (Exception e) {
            throw new CompilationException("Erro ao invocar FFmpeg", e.getMessage(), e);
        }
    }

    private String buildFilterComplex(FrameAssets frames, AudioResult audio) {
        List<Path> screenshots = frames.allScreenshots();
        long totalSecs = Math.max(1, audio.estimatedDuration().toSeconds());
        long secsPerFrame = Math.max(3, totalSecs / screenshots.size());

        StringBuilder fc = new StringBuilder();

        fc.append("[0:v]scale=")
          .append(frames.resolution().width()).append(":").append(frames.resolution().height())
          .append(",setsar=1[bg];");

        for (int i = 0; i < screenshots.size(); i++) {
            fc.append("[").append(i + 2).append(":v]")
              .append("scale=").append(frames.resolution().width())
              .append(":").append(frames.resolution().height())
              .append("[s").append(i).append("];");
        }

        String prev = "bg";
        for (int i = 0; i < screenshots.size(); i++) {
            long start = (long) i * secsPerFrame;
            long end = start + secsPerFrame;
            String label = (i == screenshots.size() - 1) ? "out" : "v" + i;
            fc.append("[").append(prev).append("][s").append(i).append("]")
              .append("overlay=0:0:enable='between(t,").append(start).append(",").append(end).append(")'")
              .append("[").append(label).append("]");
            if (i < screenshots.size() - 1) fc.append(";");
            prev = label;
        }

        return fc.toString();
    }
}
