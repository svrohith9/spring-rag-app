package com.example.rag.repository;

import com.example.rag.entity.DocumentChunkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunkEntity, UUID> {

    List<DocumentChunkEntity> findByDocumentIdOrderByChunkIndex(UUID documentId);

    long countByDocumentId(UUID documentId);
}
