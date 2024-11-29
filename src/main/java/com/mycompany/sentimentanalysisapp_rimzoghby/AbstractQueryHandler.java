/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sentimentanalysisapp_rimzoghby;

/**
 *
 * @author rimzoghby
 */
public abstract class AbstractQueryHandler {
    public abstract String processQuery(String query);  // Process the query (retrieve sentiment or analyze).
    public abstract void storeQueryInDatabase(String query, String sentiment);  // Store query and sentiment.
}
