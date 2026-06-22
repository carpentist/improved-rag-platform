package com.improvedragplatform.ingestion;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import org.springframework.stereotype.Component;

@Component
public class DocLoader {
    public Document load(String filePath) {
        return FileSystemDocumentLoader.loadDocument(filePath);
    }
}
