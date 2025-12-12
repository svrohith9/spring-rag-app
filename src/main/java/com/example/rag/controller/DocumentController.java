package com.example.rag.controller;

import com.example.rag.dto.DocumentChunkResponse;
import com.example.rag.dto.DocumentResponse;
import com.example.rag.dto.IngestResponse;
import com.example.rag.service.DocumentService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@Validated
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<IngestResponse> ingest(@NotNull @RequestParam("file") MultipartFile file) {
        IngestResponse response = documentService.ingest(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<DocumentResponse> list() {
        return documentService.listDocuments();
    }

    @GetMapping("/{id}")
    public DocumentResponse get(@PathVariable UUID id) {
        return documentService.getDocument(id);
    }

    @GetMapping("/{id}/chunks")
    public List<DocumentChunkResponse> chunks(@PathVariable UUID id) {
        return documentService.getChunks(id);
    }
}
