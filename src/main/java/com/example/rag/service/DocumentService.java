package com.example.rag.service;

import com.example.rag.dto.DocumentChunkResponse;
import com.example.rag.dto.DocumentResponse;
import com.example.rag.dto.IngestResponse;
import com.example.rag.entity.DocumentChunkEntity;
import com.example.rag.entity.DocumentEntity;
import com.example.rag.exception.BadRequestException;
import com.example.rag.exception.ResourceNotFoundException;
import com.example.rag.repository.DocumentChunkRepository;
import com.example.rag.repository.DocumentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository chunkRepository;
    private final DocumentParser parser;
    private final TextChunker chunker;
    private final EmbeddingService embeddingService;

    public DocumentService(DocumentRepository documentRepository,
                           DocumentChunkRepository chunkRepository,
                           DocumentParser parser,
                           TextChunker chunker,
                           EmbeddingService embeddingService) {
        this.documentRepository = documentRepository;
        this.chunkRepository = chunkRepository;
        this.parser = parser;
        this.chunker = chunker;
        this.embeddingService = embeddingService;
    }

    @Transactional
    public IngestResponse ingest(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        String text = parser.extractText(file);
        List<String> chunks = chunker.chunk(text);
        if (chunks.isEmpty()) {
            throw new BadRequestException("No readable content found in file");
        }

        DocumentEntity document = new DocumentEntity();
        document.setTitle(resolveTitle(file));
        document.setOriginalFileName(file.getOriginalFilename());
        document.setContentType(file.getContentType());
        document.setSizeBytes(file.getSize());
        document.setCreatedAt(Instant.now());
        document.setUpdatedAt(Instant.now());
        DocumentEntity saved = documentRepository.save(document);

        int index = 0;
        for (String chunkText : chunks) {
            DocumentChunkEntity chunkEntity = new DocumentChunkEntity();
            chunkEntity.setDocument(saved);
            chunkEntity.setChunkIndex(index++);
            chunkEntity.setText(chunkText);
            chunkEntity.setEmbedding(embeddingService.embed(chunkText));
            chunkRepository.save(chunkEntity);
        }

        return new IngestResponse(saved.getId(), chunks.size());
    }

    public List<DocumentResponse> listDocuments() {
        return documentRepository.findAll().stream()
                .sorted(Comparator.comparing(DocumentEntity::getCreatedAt).reversed())
                .map(doc -> toDto(doc, chunkRepository.countByDocumentId(doc.getId())))
                .toList();
    }

    public DocumentResponse getDocument(UUID id) {
        DocumentEntity document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document %s not found".formatted(id)));
        return toDto(document, chunkRepository.countByDocumentId(document.getId()));
    }

    public List<DocumentChunkResponse> getChunks(UUID documentId) {
        if (!documentRepository.existsById(documentId)) {
            throw new ResourceNotFoundException("Document %s not found".formatted(documentId));
        }
        return chunkRepository.findByDocumentIdOrderByChunkIndex(documentId)
                .stream()
                .map(c -> new DocumentChunkResponse(c.getId(), c.getChunkIndex(), c.getText()))
                .toList();
    }

    private DocumentResponse toDto(DocumentEntity entity, long chunkCount) {
        return new DocumentResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getOriginalFileName(),
                entity.getContentType(),
                entity.getSizeBytes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                (int) chunkCount
        );
    }

    private String resolveTitle(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename != null && filename.contains(".")) {
            return filename.substring(0, filename.lastIndexOf('.'));
        }
        if (filename != null) {
            return filename;
        }
        return "document-" + System.currentTimeMillis();
    }
}
