package com.covas;

import com.covas.adapter.ffmpeg.FfmpegVideoCompiler;
import com.covas.adapter.reddit.RedditStoryFetcher;
import com.covas.adapter.renderer.HtmlFrameRenderer;
import com.covas.adapter.tts.ElevenLabsTtsStrategy;
import com.covas.adapter.video.LocalBackgroundVideoProvider;
import com.covas.model.PipelineResult;
import com.covas.model.TtsOptions;
import com.covas.pipeline.PipelineOrchestrator;

import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== A iniciar o Pipeline do Reddit para TikTok ===");

        // 1. Configurar opções base (Imutáveis)
        TtsOptions ttsOptions = TtsOptions.builder()
                .voiceId("21m00Tcm4TlvDq8ikWAM") // Voz da Rachel no ElevenLabs
                .modelId("eleven_multilingual_v2")
                .build();

        // 2. Instanciar os Adaptadores (Dependências)
        RedditStoryFetcher fetcher = new RedditStoryFetcher();
        ElevenLabsTtsStrategy tts = new ElevenLabsTtsStrategy();
        HtmlFrameRenderer renderer = new HtmlFrameRenderer();
        LocalBackgroundVideoProvider videoProvider = new LocalBackgroundVideoProvider();
        FfmpegVideoCompiler compiler = new FfmpegVideoCompiler();

        // 3. Injetar as dependências no Orquestrador
        PipelineOrchestrator orchestrator = new PipelineOrchestrator(
                fetcher, tts, renderer, videoProvider, compiler, ttsOptions
        );

        // 4. Correr o pipeline
        Path outputDirectory = Path.of("output_videos");
        
        System.out.println("A procurar histórias e a processar...");
        
        // Vai dar erro de "Timeout" porque programámos o RedditStoryFetcher para lançar uma exceção de propósito para testar!
        List<PipelineResult> resultados = orchestrator.run("programacao", 1, outputDirectory);

       // 5. Imprimir o resumo usando o nosso switch com Pattern Matching!
        for (PipelineResult resultado : resultados) {
            orchestrator.printSummary(resultado);
            
            
            // --- CÓDIGO DE DEBUG TEMPORÁRIO ---
            if (resultado instanceof PipelineResult.Failure f && f.cause() != null) {
                System.out.println("\n--- DETALHES DO ERRO PARA DEBUG ---");
                f.cause().printStackTrace(); 
                System.out.println("-----------------------------------\n");
            }
        }
    }
}