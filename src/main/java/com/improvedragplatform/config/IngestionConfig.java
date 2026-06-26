package com.improvedragplatform.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "rag.ingestion")
public class IngestionConfig {
    /**
     * 默认分块策略：recursive / paragraph / sentence
     */
    private String defaultStrategy = "recursive";
    private String defaultSource = "legacy";

    /**
     * 递归分块：每块最大字符数
     */
    private int recursiveMaxChars = 500;

    /**
     * 递归分块：相邻块重叠字符数
     */
    private int recursiveOverlap = 100;

    /**
     * 段落分块：每块最大字符数
     */
    private int paragraphMaxChars = 500;

    /**
     * 段落分块：相邻块重叠字符数
     */
    private int paragraphOverlap = 100;

    /**
     * 句子分块：每块最大字符数
     */
    private int sentenceMaxChars = 300;

    /**
     * 句子分块：相邻块重叠字符数
     */
    private int sentenceOverlap = 50;

}
