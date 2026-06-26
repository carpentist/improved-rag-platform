package com.improvedragplatform.ingestion;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MetadataEnricher {
    public List<TextSegment> enrich(Document doc, List<TextSegment> segments) {
        return enrich(doc, segments, "legacy");
    }

    public List<TextSegment> enrich(Document doc, List<TextSegment> segments, String sourceName) {
        String docName = doc.metadata().getString(Document.FILE_NAME);
        int total = segments.size();
        List<TextSegment> enriched = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            TextSegment textSegment = segments.get(i);
            Map<String, Object> meta = new HashMap<>();
            meta.put("file_name", docName);
            meta.put("source", sourceName);
            meta.put("chunk_index", i);
            meta.put("total_chunks", total);
            enriched.add(TextSegment.from(textSegment.text(), Metadata.from(meta)));
        }
        return enriched;
    }
}
