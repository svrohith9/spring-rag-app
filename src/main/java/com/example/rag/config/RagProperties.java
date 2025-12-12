package com.example.rag.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "rag")
public class RagProperties {

    private OllamaProperties ollama = new OllamaProperties();
    private ChunkingProperties chunking = new ChunkingProperties();
    private int topK = 5;

    @Getter
    @Setter
    public static class OllamaProperties {
        private String baseUrl = "http://localhost:11434";
        private String model = "llama2";
        private double temperature = 0.2;
        private int timeoutSeconds = 120;
    }

    @Getter
    @Setter
    public static class ChunkingProperties {
        private int maxTokens = 500;
        private int overlap = 50;
    }
}
