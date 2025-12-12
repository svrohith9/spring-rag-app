package com.example.rag.service;

import com.example.rag.config.RagProperties;
import com.example.rag.dto.QueryRequest;
import com.example.rag.dto.QueryResponse;
import com.example.rag.entity.DocumentChunkEntity;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final RetrievalService retrievalService;
    private final ChatLanguageModel chatLanguageModel;
    private final RagProperties properties;

    public RagService(RetrievalService retrievalService, ChatLanguageModel chatLanguageModel, RagProperties properties) {
        this.retrievalService = retrievalService;
        this.chatLanguageModel = chatLanguageModel;
        this.properties = properties;
    }

    public QueryResponse answer(QueryRequest request) {
        int topK = request.topK() != null ? request.topK() : properties.getTopK();
        List<DocumentChunkEntity> chunks = retrievalService.findRelevantChunks(request.question(), topK);

        List<String> context = chunks.stream()
                .map(DocumentChunkEntity::getText)
                .toList();

        String prompt = buildPrompt(request.question(), context);
        String answer = chatLanguageModel.generate(prompt);
        return new QueryResponse(request.question(), answer, context);
    }

    private String buildPrompt(String question, List<String> contextChunks) {
        StringBuilder builder = new StringBuilder();
        builder.append("You are a helpful assistant. Answer the question using ONLY the provided context. ")
                .append("If the answer is not present, respond with \"I don't know.\"")
                .append(System.lineSeparator())
                .append("Context:");
        if (CollectionUtils.isEmpty(contextChunks)) {
            builder.append(" None provided.");
        } else {
            for (int i = 0; i < contextChunks.size(); i++) {
                builder.append(System.lineSeparator())
                        .append("- Chunk ").append(i + 1).append(": ")
                        .append(contextChunks.get(i));
            }
        }
        builder.append(System.lineSeparator())
                .append("Question: ")
                .append(question)
                .append(System.lineSeparator())
                .append("Answer:");
        return builder.toString();
    }
}
