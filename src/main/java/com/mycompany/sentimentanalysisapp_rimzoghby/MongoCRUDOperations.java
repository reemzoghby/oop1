package com.mycompany.sentimentanalysisapp_rimzoghby;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentClass;
import edu.stanford.nlp.util.CoreMap;
import org.bson.Document;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;


public class MongoCRUDOperations {
    private MongoDatabase database;

    // Set up Stanford NLP pipeline
    private static StanfordCoreNLP pipeline;

    static {
        // Initialize the Stanford NLP pipeline
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, sentiment");
        pipeline = new StanfordCoreNLP(props);
    }

    // Constructor to establish database connection
    public MongoCRUDOperations() {
        this.database = MongoDBConnection.connectToDatabase();
    }

    /**
     * Analyze the sentiment of a query (sentence) using Stanford NLP.
     * @param query the sentence to analyze
     * @return the sentiment classification ("Positive", "Negative", "Neutral")
     */
    public String analyzeSentiment(String query) {
        // Create an annotation for the entire query
        Annotation document = new Annotation(query);
        pipeline.annotate(document);

        // Variable to accumulate sentiment scores
        int sentimentScore = 0;

        // Analyze each sentence in the query using Stanford NLP
        for (CoreMap sentence : document.get(SentencesAnnotation.class)) {
            // Get Stanford NLP's sentence sentiment classification
            String sentiment = sentence.get(SentimentClass.class);
            System.out.println("Sentence sentiment from Stanford NLP: " + sentiment); // Debug

            // Map the Stanford sentiment to a numerical score
            switch (sentiment.toLowerCase()) {
                case "very positive":
                    sentimentScore += 2;
                    break;
                case "positive":
                    sentimentScore += 1;
                    break;
                case "neutral":
                    // No change
                    break;
                case "negative":
                    sentimentScore -= 1;
                    break;
                case "very negative":
                    sentimentScore -= 2;
                    break;
                default:
                    // Handle unexpected sentiment classes if any
                    break;
            }
        }

        // Debugging output for sentiment score
        System.out.println("Total sentiment score: " + sentimentScore);

        // Revised sentiment classification thresholds
        if (sentimentScore >= 1) {  // Positive
            return "Positive";
        } else if (sentimentScore <= -1) {  // Negative
            return "Negative";
        } else {  // Neutral
            return "Neutral";
        }
    }

    // Insert query into the database
  public void insertQuery(String query, String sentiment) {
    query = query.trim();  // Trim spaces

    System.out.println("Attempting to insert query: " + query + " with sentiment: " + sentiment);

    try {
        MongoCollection<Document> collection = database.getCollection("queries");

        // Check if the query exists
        Document existingQuery = collection.find(new Document("query", query)).first();
        if (existingQuery != null) {
            System.out.println("Duplicate query found: " + query);
            return;
        }

        // Prepare the document
        Document queryDoc = new Document("query", query)
                .append("sentiment", sentiment);

        // Insert into MongoDB
        collection.insertOne(queryDoc);
        System.out.println("Query successfully inserted.");
    } catch (Exception e) {
        System.out.println("Error during insertion: " + e.getMessage());
    }
}

    // Method to load queries from a file
    public List<String> loadQueriesFromFile(String filePath) {
        List<String> queries = new ArrayList<>();
        try {
            // Read queries from a text file (one query per line)
            queries = Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return queries;
    }

    // Method to analyze and insert queries automatically
    public void analyzeAndInsertQueries(String filePath) {
        List<String> queries = loadQueriesFromFile(filePath);
        for (String query : queries) {
            String sentiment = analyzeSentiment(query);
            insertQuery(query, sentiment);  // Automatically insert query and sentiment into MongoDB
        }
    }

    // Find sentiment based on the query (case-insensitive search)
    public String findSentiment(String query) {
        query = query.trim();  // Trim spaces

        // Debug: Check the search query
        System.out.println("Searching for query: " + query);

        MongoCollection<Document> collection = database.getCollection("queries");

        // Search for the query (case-insensitive)
        Document foundQuery = collection.find(new Document("query", new Document("$regex", "^" + Pattern.quote(query) + "$").append("$options", "i"))).first();

        if (foundQuery != null) {
            System.out.println("Found sentiment: " + foundQuery.getString("sentiment"));
            return foundQuery.getString("sentiment");  // Return the sentiment
        } else {
            System.out.println("No results found for the query: " + query);
            return null;  // Return null if not found
        }
    }

    // Update sentiment for an existing query
    public void updateSentiment(String query) {
        query = query.trim();  // Trim spaces
        String newSentiment = analyzeSentiment(query);

        MongoCollection<Document> collection = database.getCollection("queries");
        Document filter = new Document("query", query);

        // Check if query exists before updating
        Document existingQuery = collection.find(filter).first();
        if (existingQuery == null) {
            System.out.println("Query does not exist: " + query);
            return;
        }

        Document update = new Document("$set", new Document("sentiment", newSentiment));
        collection.updateOne(filter, update);
        System.out.println("Sentiment for query \"" + query + "\" updated to: " + newSentiment);
    }

    // Delete a query from the database
    public void deleteQuery(String query) {
        query = query.trim();  // Trim spaces
        MongoCollection<Document> collection = database.getCollection("queries");

        Document filter = new Document("query", query);

        // Check if query exists before deleting
        Document existingQuery = collection.find(filter).first();
        if (existingQuery == null) {
            System.out.println("Query does not exist: " + query);
            return;
        }

        collection.deleteOne(filter);
        System.out.println("Query \"" + query + "\" deleted.");
    }

    // Optional: Show all queries (for testing purposes)
    public void showAllQueries() {
        MongoCollection<Document> collection = database.getCollection("queries");

        for (Document doc : collection.find()) {
            System.out.println(doc.toJson());
        }
    }
}
