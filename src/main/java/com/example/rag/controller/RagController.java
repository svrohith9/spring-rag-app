package com.example.rag.controller;

import com.example.rag.dto.QueryRequest;
import com.example.rag.dto.QueryResponse;
import com.example.rag.service.RagService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_VALUE)
    public QueryResponse query(@Valid @RequestBody QueryRequest request) {
        return ragService.answer(request);
    }
}
