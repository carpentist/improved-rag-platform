package com.improvedragplatform.config;

import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "milvus")
public class MilvusConfig {
    private String host;
    private Integer port;

    @Value("${langchain4j.embedding-model.dimension}")
    private int dimension;

    @Bean
    public MilvusEmbeddingStore milvusEmbeddingStore() {
        return MilvusEmbeddingStore.builder()
                .host(host)
                .port(port)
                .collectionName("knowledge_base")
                .dimension(dimension)
                .indexType(IndexType.HNSW)
                .metricType(MetricType.COSINE)
                .build();
    }
}
