package com.example.rag.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(RagProperties.class)
public class AiConfig {

    @Bean
    public ChatLanguageModel chatLanguageModel(RagProperties properties) {
        RagProperties.OllamaProperties ollama = properties.getOllama();
        return OllamaChatModel.builder()
                .baseUrl(ollama.getBaseUrl())
                .modelName(ollama.getModel())
                .temperature(ollama.getTemperature())
                .timeout(Duration.ofSeconds(ollama.getTimeoutSeconds()))
                .build();
    }

    @Bean
    public EmbeddingModel embeddingModel(RagProperties properties) {
        RagProperties.OllamaProperties ollama = properties.getOllama();
        return OllamaEmbeddingModel.builder()
                .baseUrl(ollama.getBaseUrl())
                .modelName(ollama.getEmbeddingModel())
                .timeout(Duration.ofSeconds(ollama.getTimeoutSeconds()))
                .build();
    }
}
