package com.improvedragplatform.service;

import com.improvedragplatform.retrieval.ContentAggregatorFactory;
import com.improvedragplatform.retrieval.ContentInjectorFactory;
import com.improvedragplatform.retrieval.ContentRetrieverFactory;
import com.improvedragplatform.retrieval.QueryRouterFactory;
import com.improvedragplatform.retrieval.QueryTransformerFactory;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.AugmentationRequest;
import dev.langchain4j.rag.AugmentationResult;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Metadata;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagServer {
    private final ChatModel chatModel;
    private final QueryTransformerFactory queryTransformerFactory;
    private final ContentRetrieverFactory contentRetrieverFactory;
    private final QueryRouterFactory queryRouterFactory;
    private final ContentAggregatorFactory contentAggregatorFactory;
    private final ContentInjectorFactory contentInjectorFactory;

    private DefaultRetrievalAugmentor augmentor;

    @PostConstruct
    public void init(){
        ContentRetriever retriever = contentRetrieverFactory.create();
        QueryTransformer transformer = queryTransformerFactory.identity();

        augmentor = DefaultRetrievalAugmentor.builder()
                .queryTransformer(transformer)
                .queryRouter(queryRouterFactory.single(retriever))
                .contentAggregator(contentAggregatorFactory.topN(5))
                .contentInjector(contentInjectorFactory.create())
                .build();
    }

    public String ask(String question){
        UserMessage userMessage = UserMessage.from(question);
        AugmentationRequest request = new AugmentationRequest(
                userMessage,
                Metadata.from(userMessage, null, null)
        );
        AugmentationResult result = augmentor.augment(request);
        log.info("Q: {} | Retrieved {} contents", question, result.contents().size());
        return chatModel.chat(result.chatMessage()).aiMessage().text();
    }


}
