package com.mycompany.sentimentanalysisapp_rimzoghby;

public class SentimentQueryHandler extends AbstractQueryHandler {
    private final MongoCRUDOperations crudOperations;

    public SentimentQueryHandler(MongoCRUDOperations crudOperations) {
        this.crudOperations = crudOperations;
    }

    @Override
    public String processQuery(String query) {
        String sentiment = crudOperations.getSentiment(query);
        if (sentiment == null) {
            sentiment = new StanfordSentimentAnalyzer().analyze(query);
            crudOperations.upsertSentiment(query, sentiment);
        }
        return sentiment;
    }

    @Override
    public void storeQueryInDatabase(String query, String sentiment) {
        if (sentiment != null) {
            crudOperations.upsertSentiment(query, sentiment);
        } else {
            System.out.println("Sentiment cannot be null.");
        }
    }
}
