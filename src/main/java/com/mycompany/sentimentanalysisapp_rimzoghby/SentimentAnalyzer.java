/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.sentimentanalysisapp_rimzoghby;

/**
 *
 * @author rimzoghby
 */
public interface SentimentAnalyzer {
      String analyzeSentiment(String query);  // This method will be implemented in concrete classes.

    public double getSentimentScore(String query);
}
