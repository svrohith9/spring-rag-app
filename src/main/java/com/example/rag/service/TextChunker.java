package com.example.rag.service;

import com.example.rag.config.RagProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TextChunker {

    private final RagProperties properties;

    public TextChunker(RagProperties properties) {
        this.properties = properties;
    }

    public List<String> chunk(String text) {
        int maxTokens = properties.getChunking().getMaxTokens();
        int overlap = properties.getChunking().getOverlap();

        List<String> chunks = new ArrayList<>();
        String[] sentences = text.split("(?<=[.!?])\\s+");

        StringBuilder current = new StringBuilder();
        for (String sentence : sentences) {
            if (current.length() + sentence.length() > maxTokens) {
                if (current.length() > 0) {
                    chunks.add(current.toString().trim());
                    current.setLength(0);
                    // apply simple overlap by reusing tail of previous chunk
                    String overlapText = chunks.get(chunks.size() - 1);
                    if (overlapText.length() > overlap) {
                        current.append(overlapText.substring(Math.max(0, overlapText.length() - overlap)));
                        current.append(" ");
                    }
                }
            }
            current.append(sentence).append(" ");
        }

        if (current.length() > 0) {
            chunks.add(current.toString().trim());
        }
        return chunks;
    }
}
