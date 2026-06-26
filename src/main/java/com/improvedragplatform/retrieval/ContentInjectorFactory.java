package com.improvedragplatform.retrieval;

import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.rag.content.injector.ContentInjector;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContentInjectorFactory {
    public ContentInjector create() {
        return DefaultContentInjector.builder()
                .metadataKeysToInclude(List.of("file_name", "source"))
                .build();
    }

    public ContentInjector withTemplate(String template) {
        return DefaultContentInjector.builder()
                .promptTemplate(PromptTemplate.from(template))
                .metadataKeysToInclude(List.of("file_name", "source"))
                .build();
    }
}
