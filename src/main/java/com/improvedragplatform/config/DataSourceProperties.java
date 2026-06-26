package com.improvedragplatform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
@Data
@Configuration
@ConfigurationProperties(prefix = "rag")
public class DataSourceProperties {
    private List<SourceConfig> datasources = new ArrayList<>();
    private MultiRecall multiRecall = new MultiRecall();
    private Reranker reranker = new Reranker();
    @Data
    public static class SourceConfig {
        private String name;
        private String collection;
        private String description;
        private int maxResults = 3;
        private double minScore = 0.7;
        private double weight = 1.0;
    }

    @Data
    public static class MultiRecall {
        private int rrfK = 60;
        private int topN = 5;
        private int expanderN = 3;
        private boolean enableReranker = true;
    }

    @Data
    public static class Reranker {
        private String model = "qwen3-rerank";
        private String baseUrl = "https://dashscope.aliyuncs.com/compatible-api/v1/reranks";
        private String apiKey;
    }
}
