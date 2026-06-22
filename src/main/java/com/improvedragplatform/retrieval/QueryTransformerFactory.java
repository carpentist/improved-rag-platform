package com.improvedragplatform.retrieval;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.DefaultQueryTransformer;
import dev.langchain4j.rag.query.transformer.ExpandingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueryTransformerFactory {
    private final ChatModel chatModel;

    public QueryTransformer compressing() {
        return new CompressingQueryTransformer(chatModel);
    }

    public QueryTransformer expanding() {
        return new ExpandingQueryTransformer(chatModel);
    }

    public QueryTransformer identity() {
        return new DefaultQueryTransformer();
    }
}
