package com.mycompany.sentimentanalysisapp_rimzoghby;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Scanner;

public class SentimentAnalysisApp_rimzoghby {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MongoCRUDOperations queryHandler = new MongoCRUDOperations();
        FileParser fileParser = new FileParser();
        ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper for JSON

        System.out.println("Welcome to the Sentiment Analysis App!");

        while (true) {
            // Prompt the user to choose between query analysis or file analysis
            System.out.println("Would you like to:");
            System.out.println("1. Enter a query for sentiment analysis and interact with MongoDB.");
            System.out.println("2. Enter a file path to analyze a document (PDF or Word).");
            System.out.println("Type 'exit' to quit.");

            String choice = scanner.nextLine().trim();

            // Exit condition
            if (choice.equalsIgnoreCase("exit")) {
                System.out.println("Exiting the application...");
                break;
            }

            if (choice.equals("1")) {
                // Handle query-based sentiment analysis and MongoDB interaction
                System.out.println("Enter a query (or type 'exit' to quit):");
                String query = scanner.nextLine().trim();

                if (query.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting the application...");
                    break;
                }

                // Ask the user for further actions (Insert, Find, Update, Delete)
                System.out.println("Select an option:");
                System.out.println("1. Insert query");
                System.out.println("2. Find sentiment for a query");
                System.out.println("3. Update sentiment for a query");
                System.out.println("4. Delete query from the database");

                int actionChoice = scanner.nextInt();
                scanner.nextLine();  // Consume the newline character

                switch (actionChoice) {
                    case 1:
                        // Insert query and analyze sentiment
                        String sentiment = queryHandler.analyzeSentiment(query);  // Get sentiment
                        queryHandler.insertQuery(query, sentiment);  // Insert query and sentiment into MongoDB

                        // Create JSON response
                        try {
                            String jsonResponse = objectMapper.writeValueAsString(new SentimentResponse(query, sentiment));
                            System.out.println("JSON Response: " + jsonResponse);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        // Find sentiment for a query
                        String sentimentFound = queryHandler.findSentiment(query);
                        if (sentimentFound != null) {
                            // Create JSON response
                            try {
                                String jsonResponse = objectMapper.writeValueAsString(new SentimentResponse(query, sentimentFound));
                                System.out.println("JSON Response: " + jsonResponse);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("No results found for the query: " + query);
                        }
                        break;
                    case 3:
                        // Update sentiment for a query
                        queryHandler.updateSentiment(query);
                        break;
                    case 4:
                        // Delete query from the database
                        queryHandler.deleteQuery(query);
                        break;
                    default:
                        System.out.println("Invalid option! Please try again.");
                        break;
                }
            } else if (choice.equals("2")) {
                // Handle file-based sentiment analysis
                System.out.println("Enter the file path of the document (PDF or Word) to analyze:");
                String filePath = scanner.nextLine().trim();

                // Analyze sentiment for the file content
                String sentiment = fileParser.analyzeFileSentiment(filePath);

                // Display the sentiment result and create a JSON response
                try {
                    String jsonResponse = objectMapper.writeValueAsString(new SentimentResponse(filePath, sentiment));
                    System.out.println("JSON Response: " + jsonResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Invalid choice! Please select option 1 or 2.");
            }
        }

        scanner.close();
    }

    // Inner class to represent the sentiment response in JSON format
    static class SentimentResponse {
        private String input;
        private String sentiment;

        public SentimentResponse(String input, String sentiment) {
            this.input = input;
            this.sentiment = sentiment;
        }

        public String getInput() {
            return input;
        }

        public void setInput(String input) {
            this.input = input;
        }

        public String getSentiment() {
            return sentiment;
        }

        public void setSentiment(String sentiment) {
            this.sentiment = sentiment;
        }
    }
}
