package com.mycompany.sentimentanalysisapp_rimzoghby;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class MongoCRUDOperations implements CRUDOperations {

    private final MongoCollection<Document> collection;

    public MongoCRUDOperations(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    @Override
    public void create(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null.");
        }
        collection.insertOne(document);
    }

    @Override
    public Document read(String key, String value) {
        return collection.find(new Document(key, value)).first();
    }

    @Override
    public void update(String key, String value, Document updatedFields) {
        if (updatedFields == null) {
            throw new IllegalArgumentException("Updated fields cannot be null.");
        }
        collection.updateOne(new Document(key, value), new Document("$set", updatedFields));
    }

    @Override
    public void delete(String key, String value) {
        collection.deleteOne(new Document(key, value));
    }

    @Override
    public void upsertSentiment(String query, String sentiment) {
        Document existingDocument = read("query", query);
        if (existingDocument == null) {
            Document newDocument = new Document("query", query).append("sentiment", sentiment);
            create(newDocument);
        } else {
            update("query", query, new Document("sentiment", sentiment));
        }
    }

    @Override
    public String getSentiment(String query) {
        Document document = read("query", query);
        return document != null ? document.getString("sentiment") : null;
    }
}
