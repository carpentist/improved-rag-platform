package com.improvedragplatform.retrieval.scoringModel;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.scoring.ScoringModel;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

import java.util.*;

@Slf4j
@Builder
public class DashScopeRerankerScoringModel implements ScoringModel {

    @Builder.Default
    private final String baseUrl = "https://dashscope.aliyuncs.com/compatible-api/v1/reranks";

    private final String apiKey;

    @Builder.Default
    private final String modelName = "qwen3-rerank";

    private final RestClient restClient;

    @Override
    public Response<List<Double>> scoreAll(List<TextSegment> segments, String query) {
        List<String> documents = segments.stream().map(TextSegment::text).toList();

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("model", modelName);
        requestBody.put("query", query);
        requestBody.put("documents", documents);
        requestBody.put("top_n", segments.size());

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri(baseUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

            double[] scores = new double[segments.size()];
            for (Map<String, Object> r : results) {
                int idx = ((Number) r.get("index")).intValue();
                double score = ((Number) r.get("relevance_score")).doubleValue();
                scores[idx] = score;
            }
            List<Double> scoreList = new ArrayList<>(segments.size());
            for (double s : scores) {
                scoreList.add(s);
            }
            return Response.from(scoreList);
        } catch (Exception e) {
            log.error("DashScope reranker failed: {}", e.getMessage(), e);
            return Response.from(Collections.nCopies(segments.size(), 0.0));
        }
    }
}
