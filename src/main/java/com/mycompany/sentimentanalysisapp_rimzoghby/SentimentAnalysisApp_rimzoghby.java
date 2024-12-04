package com.mycompany.sentimentanalysisapp_rimzoghby;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.Scanner;

public class SentimentAnalysisApp_rimzoghby {

    public static void main(String[] args) {
        MongoCollection<Document> collection = MongoClients.create("mongodb://localhost:27017/")
                                                           .getDatabase("SentimentAnalysis")
                                                           .getCollection("queries");

        MongoCRUDOperations crudOperations = new MongoCRUDOperations(collection);
        SentimentQueryHandler queryHandler = new SentimentQueryHandler(crudOperations);
        FileParser fileParser = new FileParser();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Sentiment Analysis App!");

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Analyze a text query");
            System.out.println("2. Analyze a file");
            System.out.println("3. Exit");

            String choice = scanner.nextLine().trim();
            if (choice.equalsIgnoreCase("exit") || choice.equals("3")) {
                System.out.println("Exiting...");
                break;
            }

            switch (choice) {
                case "1":
                    System.out.print("Enter your query: ");
                    String query = scanner.nextLine().trim();

                    // Analyze sentiment
                    String sentiment = queryHandler.processQuery(query);
                    System.out.println("Sentiment: " + sentiment);

                    // Prompt user for CRUD operations
                    System.out.println("Select an operation:");
                    System.out.println("1. Insert this query and sentiment");
                    System.out.println("2. Find sentiment for this query");
                    System.out.println("3. Update sentiment for this query");
                    System.out.println("4. Delete this query from the database");

                    String operation = scanner.nextLine().trim();

                    switch (operation) {
                        case "1": // Insert
                            crudOperations.upsertSentiment(query, sentiment);
                            System.out.println("Query and sentiment saved/updated in the database.");
                            break;

                        case "2": // Find
                            String foundSentiment = crudOperations.getSentiment(query);
                            if (foundSentiment != null) {
                                System.out.println("Found sentiment: " + foundSentiment);
                            } else {
                                System.out.println("No sentiment found for the query.");
                            }
                            break;

                        case "3": // Update
                            System.out.print("Enter new sentiment: ");
                            String newSentiment = scanner.nextLine().trim();
                            crudOperations.upsertSentiment(query, newSentiment);
                            System.out.println("Sentiment updated in the database.");
                            break;

                        case "4": // Delete
                            crudOperations.delete("query", query);
                            System.out.println("Query deleted from the database.");
                            break;

                        default:
                            System.out.println("Invalid option. No operation performed.");
                    }
                    break;

                case "2":
                    System.out.print("Enter file path: ");
                    String filePath = scanner.nextLine().trim();
                    try {
                        // Parse file content
                        String content = fileParser.parseFile(filePath);

                        // Analyze sentiment
                        String fileSentiment = new StanfordSentimentAnalyzer().analyze(content);
                        System.out.println("Sentiment: " + fileSentiment);

                        // Prompt user for CRUD operations
                        System.out.println("Select an operation:");
                        System.out.println("1. Insert this file content and sentiment");
                        System.out.println("2. Skip saving to the database");

                        operation = scanner.nextLine().trim();
                        if (operation.equals("1")) {
                            crudOperations.upsertSentiment(filePath, fileSentiment);
                            System.out.println("File content and sentiment saved/updated in the database.");
                        } else if (operation.equals("2")) {
                            System.out.println("Skipping database save.");
                        } else {
                            System.out.println("Invalid option. Skipping database save.");
                        }
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
        scanner.close();
    }
}
