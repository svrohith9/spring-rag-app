package com.example.rag.dto;

import java.util.UUID;

public record DocumentChunkResponse(
        UUID id,
        int chunkIndex,
        String text
) {
}
