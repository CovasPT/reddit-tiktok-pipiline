package com.covas.adapter.renderer;

import com.covas.model.*;
import com.covas.port.FrameRenderer;
import com.covas.exception.RenderException;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class HtmlFrameRenderer implements FrameRenderer {
    private final Browser browser;

    public HtmlFrameRenderer(Browser browser) {
        this.browser = Objects.requireNonNull(browser, "Browser não pode ser nulo");
    }

    @Override
    public FrameAssets render(StoryContent story, Resolution resolution) {
        try (Page page = browser.newPage()) {
            page.setViewportSize(resolution.width(), resolution.height());

            page.setContent(buildTitleHtml(story));
            Path titleImg = Files.createTempFile("title_" + story.id() + "_", ".png");
            page.screenshot(new Page.ScreenshotOptions().setPath(titleImg));

            List<Path> commentScreenshots = new ArrayList<>();
            List<String> comments = story.topComments();
            for (int i = 0; i < comments.size(); i++) {
                page.setContent(buildCommentHtml(comments.get(i)));
                Path commentImg = Files.createTempFile("comment_" + i + "_" + story.id() + "_", ".png");
                page.screenshot(new Page.ScreenshotOptions().setPath(commentImg));
                commentScreenshots.add(commentImg);
            }

            return new FrameAssets(titleImg, commentScreenshots, resolution);
        } catch (IOException e) {
            throw new RenderException("Erro ao criar ficheiro temporário para screenshot", e);
        } catch (Exception e) {
            throw new RenderException("Falha ao renderizar frames", e);
        }
    }

    private String buildTitleHtml(StoryContent story) {
        return "<html><body style='background:#1a1a1b;color:white;font-family:sans-serif;"
                + "display:flex;align-items:center;justify-content:center;"
                + "height:100vh;margin:0;padding:40px;box-sizing:border-box'>"
                + "<div><h1 style='font-size:48px;text-align:center'>" + escape(story.title()) + "</h1>"
                + "<p style='text-align:center;color:#818384'>r/" + escape(story.subreddit())
                + " • " + story.score() + " upvotes</p></div></body></html>";
    }

    private String buildCommentHtml(String comment) {
        return "<html><body style='background:#1a1a1b;color:white;font-family:sans-serif;"
                + "display:flex;align-items:center;justify-content:center;"
                + "height:100vh;margin:0;padding:60px;box-sizing:border-box'>"
                + "<p style='font-size:36px;text-align:center;line-height:1.5'>"
                + escape(comment) + "</p></body></html>";
    }

    private String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}