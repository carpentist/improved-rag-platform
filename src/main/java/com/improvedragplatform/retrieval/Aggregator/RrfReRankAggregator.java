package com.improvedragplatform.retrieval.Aggregator;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.scoring.ScoringModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.ContentMetadata;
import dev.langchain4j.rag.content.aggregator.ContentAggregator;
import dev.langchain4j.rag.content.aggregator.ReciprocalRankFuser;
import dev.langchain4j.rag.query.Query;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Builder
public class RrfReRankAggregator implements ContentAggregator {
    @Builder.Default
    private final int rrfK = 60;
    @Builder.Default
    private final int topN = 5;
    private final ScoringModel scoringModel;
    @Builder.Default
    private final double minScore = 0.;


    @Override
    public List<Content> aggregate(Map<Query, Collection<List<Content>>> queryToContents) {
        List<List<Content>> allRankedLists = new ArrayList<>();
        Query primaryQuery = null;
        for (Map.Entry<Query, Collection<List<Content>>> entry : queryToContents.entrySet()) {
            if (primaryQuery == null) {
                primaryQuery = entry.getKey();
            }
            for (List<Content> list : entry.getValue()) {
                if (!list.isEmpty()) {
                    allRankedLists.add(new ArrayList<>(list));
                }
            }
        }
        if (allRankedLists.isEmpty()) {
            log.warn("No results from any retriever");
            return Collections.emptyList();
        }
        // RRF 融合
        List<Content> fused = ReciprocalRankFuser.fuse(allRankedLists, rrfK);
        log.debug("RRF fused {} lists into {} candidates", allRankedLists.size(), fused.size());
        // 重排序
        List<Content> scored;
        if (scoringModel != null && primaryQuery != null && !fused.isEmpty()) {
            scored = rerank(fused, primaryQuery);
        } else {
            scored = fused;
        }

        List<Content> result = scored.stream()
                .filter(content -> {
                    Double score = (Double) content.metadata().get(ContentMetadata.SCORE);
                    return score != null && score >= minScore;
                })
                .limit(topN)
                .toList();
        log.info("Aggregated: {} fused -> {} final (topN={})", fused.size(), result.size(), topN);
        logSourceContributions(result);
        return result;
    }

    private List<Content> rerank(List<Content> fused, Query query) {
        List<TextSegment> segments = fused.stream()
                .map(c -> TextSegment.from(c.textSegment().text()))
                .collect(Collectors.toList());
        Response<List<Double>> response = scoringModel.scoreAll(segments, query.text());
        if (response == null || response.content() == null) {
            log.warn("Reranker returned null, falling back to RRF-only scores");
            return fused;
        }
        List<Double> scores = response.content();
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < Math.min(fused.size(), scores.size()); i++) {
            indices.add(i);
        }
        indices.sort((a, b) -> Double.compare(scores.get(b), scores.get(a)));
        List<Content> reranked = new ArrayList<>();
        for (int idx : indices) {
            reranked.add(fused.get(idx));
        }
        return reranked;
    }

    private void logSourceContributions(List<Content> results) {
        Map<String, Long> sourceCounts = results.stream().collect(
                Collectors.groupingBy(
                        content -> Objects.toString(content.textSegment().metadata().getString("source"), "unknown"),
                        Collectors.counting()
                )
        );
        sourceCounts.forEach((source, count) ->
                log.info("  Source '{}': {} results", source, count));
    }
}
