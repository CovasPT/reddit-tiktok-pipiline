package com.covas.port;

import com.covas.model.AudioResult;
import com.covas.model.TtsOptions;

public interface TtsStrategy {
    
    /**
     * Sintetiza o texto em áudio usando as opções fornecidas.
     */
    AudioResult synthesize(String text, TtsOptions opts);

    /**
     * Identificador do provider (ex: "elevenlabs", "tiktok-tts") para efeitos de log.
     */
    String providerId();
}