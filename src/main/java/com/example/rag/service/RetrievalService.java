package com.example.rag.service;

import com.example.rag.entity.DocumentChunkEntity;
import com.example.rag.repository.DocumentChunkRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class RetrievalService {

    private final DocumentChunkRepository chunkRepository;
    private final EmbeddingService embeddingService;

    public RetrievalService(DocumentChunkRepository chunkRepository, EmbeddingService embeddingService) {
        this.chunkRepository = chunkRepository;
        this.embeddingService = embeddingService;
    }

    public List<DocumentChunkEntity> findRelevantChunks(String question, int topK) {
        double[] queryEmbedding = embeddingService.embed(question);
        List<DocumentChunkEntity> all = chunkRepository.findAll();
        return all.stream()
                .sorted(Comparator.comparingDouble(chunk -> -cosineSimilarity(queryEmbedding, chunk.getEmbedding())))
                .limit(topK)
                .toList();
    }

    private double cosineSimilarity(double[] a, double[] b) {
        if (a == null || b == null || a.length == 0 || b.length == 0 || a.length != b.length) {
            return -1.0;
        }
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB) + 1e-10);
    }
}
