package com.improvedragplatform.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSourceRegistry {
    private final MilvusServiceClient milvusServiceClient;
    private final EmbeddingModel embeddingModel;
    private final DataSourceProperties dataSourceProperties;

    @Value("${langchain4j.embedding-model.dimension}")
    private int dimension;
    @Value("${rag.ingestion.default-source}")
    private String DEFAULT_SOURCE_NAME;

    @Getter
    private final Map<String, MilvusEmbeddingStore> stores = new ConcurrentHashMap<>();
    @Getter
    private final Map<String, ContentRetriever> retrievers = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        for (DataSourceProperties.SourceConfig sc : dataSourceProperties.getDatasources()) {
            MilvusEmbeddingStore store = MilvusEmbeddingStore.builder()
                    .milvusClient(milvusServiceClient)
                    .collectionName(sc.getCollection())
                    .dimension(dimension)
                    .indexType(IndexType.HNSW)
                    .metricType(MetricType.COSINE)
                    .build();
            stores.put(sc.getName(), store);
            ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                    .embeddingStore(store)
                    .embeddingModel(embeddingModel)
                    .maxResults(sc.getMaxResults())
                    .minScore(sc.getMinScore())
                    .displayName(sc.getName())
                    .build();
            retrievers.put(sc.getName(), retriever);

            log.info("Registered source '{}' -> collection '{}' (maxResults={}, minScore={})",
                    sc.getName(), sc.getCollection(), sc.getMaxResults(), sc.getMinScore());
        }
    }

    public List<ContentRetriever> getAllRetrievers() {
        return new ArrayList<>(retrievers.values());
    }

    public MilvusEmbeddingStore getStore(String sourceName) {
        MilvusEmbeddingStore store = stores.get(sourceName);
        if (store == null) {
            throw new IllegalArgumentException("Unknown data source: " + sourceName);
        }
        return store;
    }

    public String getDefaultSourceName() {
        return dataSourceProperties.getDatasources().isEmpty() ? DEFAULT_SOURCE_NAME : dataSourceProperties.getDatasources().getFirst().getName();
    }

}
