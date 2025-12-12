package com.example.rag.dto;

import java.util.List;

public record QueryResponse(
        String question,
        String answer,
        List<String> context
) {
}
