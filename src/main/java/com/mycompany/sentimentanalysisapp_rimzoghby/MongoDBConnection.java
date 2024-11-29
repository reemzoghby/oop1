/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sentimentanalysisapp_rimzoghby;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
/**
 *
 * @author rimzoghby
 */



public class MongoDBConnection {
    public static MongoDatabase connectToDatabase() {
        // Connect to MongoDB locally
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/");

        // Access the SentimentAnalysis database
        MongoDatabase database = mongoClient.getDatabase("SentimentAnalysis");
        
        // Print all databases to verify connection
        for (String dbName : mongoClient.listDatabaseNames()) {
            System.out.println("Database found: " + dbName);
        }

        // Optionally, print collections in the SentimentAnalysis database
        System.out.println("Collections in SentimentAnalysis:");
        for (String collectionName : database.listCollectionNames()) {
            System.out.println(collectionName);
        }

        return database;
    }
}
