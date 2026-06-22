package com.improvedragplatform.retrieval;

import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.ContentMetadata;
import dev.langchain4j.rag.content.aggregator.ContentAggregator;
import dev.langchain4j.rag.content.aggregator.DefaultContentAggregator;
import dev.langchain4j.rag.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
public class ContentAggregatorFactory {
    public static class TopNContentAggregator implements ContentAggregator {
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

    public ContentAggregator merge() {
        return new DefaultContentAggregator();
    }

    public ContentAggregator topN(int n) {
        return new TopNContentAggregator(n);
    }
}
