package com.example.rag.dto;

import java.time.Instant;
import java.util.UUID;

public record DocumentResponse(
        UUID id,
        String title,
        String originalFileName,
        String contentType,
        long sizeBytes,
        Instant createdAt,
        Instant updatedAt,
        int chunkCount
) {
}
