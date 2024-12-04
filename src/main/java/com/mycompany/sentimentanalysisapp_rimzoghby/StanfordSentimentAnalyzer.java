package com.mycompany.sentimentanalysisapp_rimzoghby;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;

import java.util.Properties;

public class StanfordSentimentAnalyzer {

    private final StanfordCoreNLP pipeline;

    public StanfordSentimentAnalyzer() {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, sentiment");
        this.pipeline = new StanfordCoreNLP(props);
    }

    public String analyze(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "Neutral";
        }

        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        double sentimentScore = 0;
        int sentenceCount = 0;

        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            sentimentScore += getSentimentScore(sentiment);
            sentenceCount++;
        }

        sentimentScore = sentenceCount > 0 ? sentimentScore / sentenceCount : 2.0;

        return sentimentScore >= 3 ? "Positive" :
               sentimentScore <= 1 ? "Negative" : "Neutral";
    }

    private int getSentimentScore(String sentiment) {
        switch (sentiment.toLowerCase()) {
            case "very positive": return 4;
            case "positive": return 3;
            case "neutral": return 2;
            case "negative": return 1;
            case "very negative": return 0;
            default: return 2;
        }
    }
}
