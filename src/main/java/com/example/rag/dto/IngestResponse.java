package com.example.rag.dto;

import java.util.UUID;

public record IngestResponse(
        UUID documentId,
        int chunksIndexed
) {
}
