package com.improvedragplatform.ingestion;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngestionPipeline {
    private final DocLoader docLoader;
    private final DocChunker docChunker;
    private final MetadataEnricher metadataEnricher;
    private final EmbedStoreWriter embedStoreWriter;

    public int ingest(String filePath) {
        Document doc = docLoader.load(filePath);
        List<TextSegment> segments = docChunker.chunk(doc);
        segments = metadataEnricher.enrich(doc, segments);
        embedStoreWriter.write(segments);
        return segments.size();
    }

    public int ingest(String filePath, ChunkStrategy strategy) {
        Document doc = docLoader.load(filePath);
        List<TextSegment> segments = docChunker.chunk(doc, strategy);
        segments = metadataEnricher.enrich(doc, segments);
        embedStoreWriter.write(segments);
        return segments.size();
    }
}
