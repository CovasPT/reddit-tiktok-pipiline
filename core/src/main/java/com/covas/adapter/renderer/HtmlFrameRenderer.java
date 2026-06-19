package com.covas.adapter.renderer;

import com.covas.model.*;
import com.covas.port.FrameRenderer;
import com.covas.exception.RenderException;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import java.nio.file.Path;
import java.util.Objects;
import java.util.List;
import java.util.stream.IntStream;

public final class HtmlFrameRenderer implements FrameRenderer {
    private final Browser browser;

    public HtmlFrameRenderer(Browser browser) {
        this.browser = Objects.requireNonNull(browser, "Browser não pode ser nulo");
    }

    @Override
    public FrameAssets render(StoryContent story, Resolution resolution) {
        try (Page page = browser.newPage()) {
            Path titleImg = Path.of("title_" + story.id() + ".png");
            // ... (restante lógica de renderização usando page)
            return new FrameAssets(titleImg, List.of(), resolution); // simplificado para exemplo
        } catch (Exception e) {
            throw new RenderException("Falha ao renderizar frames", e);
        }
    }
}