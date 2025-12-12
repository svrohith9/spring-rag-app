package com.example.rag.service;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;

    public EmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public double[] embed(String text) {
        Response<Embedding> response = embeddingModel.embed(text);
        float[] vector = response.content().vector();
        double[] converted = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            converted[i] = vector[i];
        }
        return converted;
    }
}
