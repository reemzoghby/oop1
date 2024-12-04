/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.sentimentanalysisapp_rimzoghby;

/**
 *
 * @author rimzoghby
 */


import org.bson.Document;

public interface CRUDOperations {

    void create(Document document);

    Document read(String key, String value);

    void update(String key, String value, Document updatedFields);

    void delete(String key, String value);

    void upsertSentiment(String query, String sentiment);

    String getSentiment(String query);
}
