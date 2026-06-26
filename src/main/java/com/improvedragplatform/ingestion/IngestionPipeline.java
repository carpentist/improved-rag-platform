package com.improvedragplatform.ingestion;

import com.improvedragplatform.config.DataSourceRegistry;
import com.improvedragplatform.config.IngestionConfig;
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
    private final DataSourceRegistry registry;
    private final IngestionConfig ingestionConfig;

    public int ingest(String filePath) {

        return ingest(filePath, ingestionConfig.getDefaultSource());
    }

    public int ingest(String filePath, ChunkStrategy strategy) {
        return ingest(filePath, strategy, ingestionConfig.getDefaultSource());
    }

    public int ingest(String filePath, String sourceName) {
        return ingest(filePath, ChunkStrategy.valueOf(ingestionConfig.getDefaultStrategy().toUpperCase()), sourceName);
    }

    public int ingest(String filePath, ChunkStrategy strategy, String sourceName) {
        Document doc = docLoader.load(filePath);
        List<TextSegment> segments = docChunker.chunk(doc, strategy);
        segments = metadataEnricher.enrich(doc, segments, sourceName);
        embedStoreWriter.write(segments, registry.getStore(sourceName));
        return segments.size();
    }
}
