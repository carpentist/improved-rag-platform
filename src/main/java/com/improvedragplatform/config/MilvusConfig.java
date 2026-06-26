package com.improvedragplatform.config;

import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
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

    @Bean
    public MilvusServiceClient milvusServiceClient() {
        return new MilvusServiceClient(
                ConnectParam.newBuilder()
                        .withHost(host)
                        .withPort(port)
                        .build()
        );
    }
}
