package com.improvedragplatform.config;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RagConfig {
    public static class BatchEmbeddingModel implements EmbeddingModel{
        private final EmbeddingModel delegate;
        private static final int MAX_BATCH = 10;

        public BatchEmbeddingModel(EmbeddingModel embeddingModel){
            this.delegate = embeddingModel;
        }
        @Override
        public Response<List<Embedding>> embedAll(List<TextSegment> segments) {
            List<Embedding> all = new ArrayList<>();
            for(int i=0;i<segments.size();i+=MAX_BATCH){
                int end = Math.min(i+MAX_BATCH,segments.size());
                all.addAll(delegate.embedAll(segments.subList(i,end)).content());
            }
            return Response.from(all);
        }
    }

    @Value("${langchain4j.chat-model.base-url}")
    private String chatBaseUrl;
    @Value("${langchain4j.chat-model.model-name}")
    private String chatModelName;
    @Value("${langchain4j.chat-model.api-key}")
    private String chatApiKey;

    @Value("${langchain4j.embedding-model.base-url}")
    private String embeddingBaseUrl;
    @Value("${langchain4j.embedding-model.model-name}")
    private String embeddingModelName;
    @Value("${langchain4j.embedding-model.api-key}")
    private String embeddingApiKey;
    @Value("${langchain4j.embedding-model.dimension}")
    private int embeddingDimension;


    @Bean
    public EmbeddingModel embeddingModel(){
        OpenAiEmbeddingModel delegate = OpenAiEmbeddingModel.builder()
                .modelName(embeddingModelName)
                .baseUrl(embeddingBaseUrl)
                .apiKey(embeddingApiKey)
                .dimensions(embeddingDimension)
                .build();
        return new BatchEmbeddingModel(delegate);
    }

    @Bean
    public ChatModel chatModel(){
        return OpenAiChatModel.builder()
                .baseUrl(chatBaseUrl)
                .modelName(chatModelName)
                .apiKey(chatApiKey)
                .build();
    }
}
