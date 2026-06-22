package com.improvedragplatform.ingestion;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmbedStoreWriter {
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;

    public void write(List<TextSegment> segments) {
        if (segments.isEmpty()) {
            log.warn("Empty segments, nothing to write");
            return;
        }
        embeddingStore.addAll(
                embeddingModel.embedAll(segments).content(),
                segments
        );
        log.info("Written {} segments to vector store", segments.size());
    }
}
