package com.example.rag.service;

import com.example.rag.exception.BadRequestException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class DocumentParser {

    public String extractText(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null && contentType.contains("pdf")) {
            return parsePdf(file);
        }
        if (contentType != null && contentType.startsWith("text")) {
            return parseText(file);
        }
        String filename = file.getOriginalFilename();
        if (filename != null && filename.toLowerCase().endsWith(".pdf")) {
            return parsePdf(file);
        }
        if (filename != null && (filename.toLowerCase().endsWith(".txt") || filename.toLowerCase().endsWith(".md"))) {
            return parseText(file);
        }
        throw new BadRequestException("Only PDF and text files are supported");
    }

    private String parsePdf(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream(); PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            throw new BadRequestException("Failed to read PDF: " + e.getMessage());
        }
    }

    private String parseText(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new BadRequestException("Failed to read text file: " + e.getMessage());
        }
    }
}
