package com.improvedragplatform.retrieval.Aggregator;

import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.ContentMetadata;
import dev.langchain4j.rag.content.aggregator.ContentAggregator;
import dev.langchain4j.rag.query.Query;

import java.util.*;

public class TopNContentAggregator implements ContentAggregator {
    private final int n;

    public TopNContentAggregator(int n) {
        this.n = n;
    }

    @Override
    public List<Content> aggregate(Map<Query, Collection<List<Content>>> queryToContents) {
        List<Content> all = new ArrayList<>();
        for (Collection<List<Content>> contents : queryToContents.values()) {
            for (List<Content> list : contents) {
                all.addAll(list);
            }
        }

        all.sort(Comparator.comparingDouble((Content c) -> (Double) c.metadata().get(ContentMetadata.SCORE)).reversed());

        return all.size() > n ? all.subList(0, n) : all;
    }
}
