package com.improvedragplatform.retrieval;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.rag.query.router.LanguageModelQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class QueryRouterFactory {
    private final ChatModel chatModel;

    public QueryRouter create(Map<ContentRetriever, String> retrieverToDescription) {
        return new LanguageModelQueryRouter(chatModel, retrieverToDescription);
    }

    public QueryRouter broadcast(Collection<ContentRetriever> retrievers) {
        return new DefaultQueryRouter(retrievers);
    }

    /**
     * 单检索器：不路由，直接查
     */
    public QueryRouter single(ContentRetriever retriever) {
        return query -> List.of(retriever);
    }

}
