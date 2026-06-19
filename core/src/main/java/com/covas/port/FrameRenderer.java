package com.covas.port;

import com.covas.model.FrameAssets;
import com.covas.model.Resolution;
import com.covas.model.StoryContent;

public interface FrameRenderer {
    FrameAssets render(StoryContent story, Resolution resolution);
}