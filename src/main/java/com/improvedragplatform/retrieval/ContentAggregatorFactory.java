package com.improvedragplatform.retrieval;

import com.improvedragplatform.retrieval.Aggregator.TopNContentAggregator;
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

    public ContentAggregator merge() {
        return new DefaultContentAggregator();
    }

    public ContentAggregator topN(int n) {
        return new TopNContentAggregator(n);
    }
}
