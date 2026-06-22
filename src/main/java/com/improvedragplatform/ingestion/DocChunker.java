package com.improvedragplatform.ingestion;

import com.improvedragplatform.config.IngestionConfig;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.document.splitter.DocumentBySentenceSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DocChunker {
    private final IngestionConfig config;

    public List<TextSegment> chunk(Document doc, ChunkStrategy strategy) {
        return switch (strategy) {
            case RECURSIVE ->
                    split(doc, DocumentSplitters.recursive(config.getRecursiveMaxChars(), config.getRecursiveOverlap()));
            case PARAGRAPH ->
                    split(doc, new DocumentByParagraphSplitter(config.getParagraphMaxChars(), config.getParagraphOverlap()));
            case SENTENCE ->
                    split(doc, new DocumentBySentenceSplitter(config.getSentenceMaxChars(), config.getSentenceOverlap()));
        };
    }

    public List<TextSegment> chunk(Document doc) {
        return chunk(doc, ChunkStrategy.valueOf(config.getDefaultStrategy().toUpperCase()));
    }

    private List<TextSegment> split(Document doc, DocumentSplitter splitter) {
        return splitter.split(doc);
    }
}
