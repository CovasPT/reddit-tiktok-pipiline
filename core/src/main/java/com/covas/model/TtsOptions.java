package main.java.com.covas.model;

import java.util.Objects;

public final class TtsOptions {
    private final String voiceId;
    private final String modelId;
    private final float stability;
    private final float similarityBoost;
    private final float style;

    private TtsOptions(Builder builder) {
        this.voiceId = builder.voiceId;
        this.modelId = builder.modelId;
        this.stability = builder.stability;
        this.similarityBoost = builder.similarityBoost;
        this.style = builder.style;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String voiceId() { return voiceId; }
    public String modelId() { return modelId; }
    public float stability() { return stability; }
    public float similarityBoost() { return similarityBoost; }
    public float style() { return style; }

    public static final class Builder {
        private String voiceId;
        private String modelId;
        private float stability = 0.5f;
        private float similarityBoost = 0.75f;
        private float style = 0.0f;

        public Builder voiceId(String voiceId) {
            this.voiceId = voiceId;
            return this;
        }

        public Builder modelId(String modelId) {
            this.modelId = modelId;
            return this;
        }

        public Builder stability(float stability) {
            if (stability < 0.0f || stability > 1.0f) {
                throw new IllegalArgumentException("Stability deve estar entre 0 e 1");
            }
            this.stability = stability;
            return this;
        }

        public Builder similarityBoost(float similarityBoost) {
            if (similarityBoost < 0.0f || similarityBoost > 1.0f) {
                throw new IllegalArgumentException("SimilarityBoost deve estar entre 0 e 1");
            }
            this.similarityBoost = similarityBoost;
            return this;
        }

        public Builder style(float style) {
            if (style < 0.0f || style > 1.0f) {
                throw new IllegalArgumentException("Style deve estar entre 0 e 1");
            }
            this.style = style;
            return this;
        }

        public TtsOptions build() {
            Objects.requireNonNull(voiceId, "voiceId é obrigatório");
            Objects.requireNonNull(modelId, "modelId é obrigatório");
            return new TtsOptions(this);
        }
    }
}