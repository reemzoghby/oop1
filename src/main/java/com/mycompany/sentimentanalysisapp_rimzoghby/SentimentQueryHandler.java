/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sentimentanalysisapp_rimzoghby;

/**
 *
 * @author rimzoghby
 */

public class SentimentQueryHandler extends AbstractQueryHandler {
    MongoCRUDOperations operations = new MongoCRUDOperations();

    public String processQuery(String query) {
        // Check if query exists in the database
        String sentiment = operations.findSentiment(query);  // Return sentiment from DB if found
        
        if (sentiment == null) {
            // If not found, perform sentiment analysis and return sentiment
            sentiment = operations.analyzeSentiment(query);
        }
        return sentiment;
    }

    public void storeQueryInDatabase(String query, String sentiment) {
        if (sentiment != null) {
            // Store the query and sentiment in DB if sentiment is not null
            operations.insertQuery(query, sentiment);  // Store the query and sentiment in DB
        } else {
            // Handle case for null sentiment, if needed
            System.out.println("Query sentiment cannot be null.");
        }
    }
}
