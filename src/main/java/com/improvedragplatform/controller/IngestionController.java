package com.improvedragplatform.controller;

import com.improvedragplatform.ingestion.ChunkStrategy;
import com.improvedragplatform.ingestion.IngestionPipeline;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
public class IngestionController {

    private final IngestionPipeline ingestionPipeline;

    @PostMapping("/ingest")
    public String ingest(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "legacy") String source
    ) throws IOException {
        Path temp = Files.createTempFile("rag-ingest-", file.getOriginalFilename());
        file.transferTo(temp);
        int chunks = ingestionPipeline.ingest(temp.toString(), source);
        Files.deleteIfExists(temp);
        return "Ingested " + chunks + " chunks from " + file.getOriginalFilename()
                + " to source '" + source + "'";
    }

    @PostMapping("/ingest/{strategy}")
    public String ingestWithStrategy(
            @RequestParam("file") MultipartFile file,
            @PathVariable ChunkStrategy strategy,
            @RequestParam(defaultValue = "legacy") String source
    ) throws IOException {
        Path temp = Files.createTempFile("rag-ingest-", file.getOriginalFilename());
        file.transferTo(temp);
        int chunks = ingestionPipeline.ingest(temp.toString(), strategy, source);
        Files.deleteIfExists(temp);
        return "Ingested " + chunks + " chunks (strategy=" + strategy + ")";
    }
}
