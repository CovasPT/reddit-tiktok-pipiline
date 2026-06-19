package com.covas.web;

import com.covas.model.PipelineResult;
import com.covas.pipeline.PipelineFactory;
import com.covas.pipeline.PipelineOrchestrator;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class PipelineServer {

    private static final Path OUTPUT_DIR = Path.of("output_videos");

    private PipelineServer() {}

    public static void start(int port) throws Exception {
        Files.createDirectories(OUTPUT_DIR);

        Javalin app = Javalin.create(config ->
                config.staticFiles.add("/static", Location.CLASSPATH)
        ).start(port);

        app.post("/api/run", ctx -> {
            String subreddit = ctx.formParam("subreddit");
            String limitParam = ctx.formParam("limit");

            if (!isValidSubreddit(subreddit)) {
                ctx.status(400).json(Map.of("error", "Subreddit inválido. Usa só letras, números e _ (1-21 chars)."));
                return;
            }

            int limit = parseLimit(limitParam);

            try (Playwright pw = Playwright.create();
                 Browser browser = pw.chromium().launch()) {

                PipelineOrchestrator orchestrator = PipelineFactory.create(browser);
                List<PipelineResult> results = orchestrator.run(subreddit, limit, OUTPUT_DIR);

                ctx.json(results.stream().map(PipelineServer::toMap).toList());
            }
        });

        app.get("/api/videos", ctx -> {
            if (!Files.exists(OUTPUT_DIR)) {
                ctx.json(List.of());
                return;
            }
            try (Stream<Path> stream = Files.list(OUTPUT_DIR)) {
                List<String> videos = stream
                        .filter(p -> p.toString().endsWith(".mp4"))
                        .map(p -> p.getFileName().toString())
                        .sorted()
                        .toList();
                ctx.json(videos);
            }
        });

        app.get("/api/videos/{name}", ctx -> {
            String name = ctx.pathParam("name");
            if (!isSafeFilename(name)) {
                ctx.status(400).result("Nome de ficheiro inválido.");
                return;
            }
            Path videoPath = OUTPUT_DIR.resolve(name);
            if (!Files.exists(videoPath)) {
                ctx.status(404).result("Vídeo não encontrado.");
                return;
            }
            ctx.contentType("video/mp4");
            ctx.result(Files.newInputStream(videoPath));
        });

        System.out.println("Servidor iniciado → http://localhost:" + port);
    }

    private static boolean isValidSubreddit(String s) {
        return s != null && s.matches("[a-zA-Z0-9_]{1,21}");
    }

    private static boolean isSafeFilename(String name) {
        return name != null && name.matches("[a-zA-Z0-9_\\-\\.]+") && !name.contains("..");
    }

    private static int parseLimit(String raw) {
        try {
            return Math.max(1, Math.min(10, Integer.parseInt(raw)));
        } catch (Exception e) {
            return 3;
        }
    }

    private static Map<String, Object> toMap(PipelineResult result) {
        return switch (result) {
            case PipelineResult.Success s -> Map.of(
                    "status", "success",
                    "video", s.outputVideo().getFileName().toString(),
                    "title", s.source().title(),
                    "duration", s.processingTime().toSeconds()
            );
            case PipelineResult.Failure f -> Map.of(
                    "status", "failure",
                    "stage", f.stageId(),
                    "reason", f.reason()
            );
        };
    }
}
