package com.improvedragplatform.service;

import com.improvedragplatform.config.DataSourceProperties;
import com.improvedragplatform.config.DataSourceRegistry;
import com.improvedragplatform.retrieval.Aggregator.RrfReRankAggregator;
import com.improvedragplatform.retrieval.ContentInjectorFactory;
import com.improvedragplatform.retrieval.QueryRouterFactory;
import com.improvedragplatform.retrieval.QueryTransformerFactory;
import com.improvedragplatform.retrieval.scoringModel.DashScopeRerankerScoringModel;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.scoring.ScoringModel;
import dev.langchain4j.rag.AugmentationRequest;
import dev.langchain4j.rag.AugmentationResult;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.content.aggregator.ContentAggregator;
import dev.langchain4j.rag.content.injector.ContentInjector;
import dev.langchain4j.rag.query.Metadata;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagServer {
    private final ChatModel chatModel;
    private final QueryTransformerFactory queryTransformerFactory;
    private final ContentInjectorFactory contentInjectorFactory;
    private final DataSourceProperties dataSourceProperties;
    private final DataSourceRegistry dataSourceRegistry;
    private final QueryRouterFactory queryRouterFactory;
    private final RestClient restClient;


    private DefaultRetrievalAugmentor augmentor;

    @PostConstruct
    public void init() {
        DataSourceProperties.MultiRecall mr = dataSourceProperties.getMultiRecall();

        QueryTransformer transformer = queryTransformerFactory.expanding(mr.getExpanderN());
        QueryRouter router = queryRouterFactory.broadcast(dataSourceRegistry.getAllRetrievers());
        DataSourceProperties.Reranker rc = dataSourceProperties.getReranker();
        ScoringModel scoringModel = null;
        if (mr.isEnableReranker()) {
            scoringModel = DashScopeRerankerScoringModel.builder()
                    .apiKey(rc.getApiKey())
                    .modelName(rc.getModel())
                    .baseUrl(rc.getBaseUrl())
                    .restClient(restClient)
                    .build();
        }
        ContentAggregator aggregator = RrfReRankAggregator.builder()
                .rrfK(mr.getRrfK())
                .topN(mr.getTopN())
                .scoringModel(scoringModel)
                .build();

        ContentInjector injector = contentInjectorFactory.create();

        Executor executor = Executors.newVirtualThreadPerTaskExecutor();
        augmentor = DefaultRetrievalAugmentor.builder()
                .queryTransformer(transformer)
                .queryRouter(router)
                .contentAggregator(aggregator)
                .contentInjector(injector)
                .executor(executor)
                .build();

        log.info("Multi-recall augmentor: {} sources, expanderN={}, rrfK={}, topN={}, reranker={}",
                dataSourceRegistry.getAllRetrievers().size(), mr.getExpanderN(),
                mr.getRrfK(), mr.getTopN(),
                scoringModel != null ? "enabled" : "disabled");
    }

    public String ask(String question) {
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
