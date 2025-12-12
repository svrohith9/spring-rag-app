package com.example.rag.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "document_chunks")
@Getter
@Setter
public class DocumentChunkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id")
    private DocumentEntity document;

    @Column(name = "chunk_index")
    private int chunkIndex;

    @Column(columnDefinition = "CLOB")
    private String text;

    @Convert(converter = DoubleArrayToStringConverter.class)
    @Column(name = "embedding", columnDefinition = "CLOB")
    private double[] embedding;
}
