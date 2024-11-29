package com.mycompany.sentimentanalysisapp_rimzoghby;

import edu.stanford.nlp.ling.CoreAnnotations;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.*;
import java.util.Properties;
import org.json.JSONObject;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

public class FileParser {

    private static StanfordCoreNLP pipeline;

    static {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, sentiment");
        pipeline = new StanfordCoreNLP(props);
    }

    public String analyzeFileSentiment(String filePath) {
        String text = extractTextFromFile(filePath);
        System.out.println("Extracted Text: " + text);
        return analyzeSentiment(text);
    }

    public String extractTextFromFile(String filePath) {
        File file = new File(filePath);
        String extension = getFileExtension(file);

        if ("docx".equalsIgnoreCase(extension)) {
            return extractTextFromWord(filePath);
        } else if ("pdf".equalsIgnoreCase(extension)) {
            return extractTextFromPDF(filePath);
        } else if ("json".equalsIgnoreCase(extension)) {
            return extractTextFromJSON(filePath);
        } else {
            return "Unsupported file type.";
        }
    }

    private String extractTextFromWord(String filePath) {
        StringBuilder text = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument doc = new XWPFDocument(fis)) {

            doc.getParagraphs().forEach(p -> text.append(p.getText()).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    private String extractTextFromPDF(String filePath) {
        StringBuilder text = new StringBuilder();
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            text.append(stripper.getText(document));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    private String extractTextFromJSON(String filePath) {
        StringBuilder text = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            // Parse JSON and extract the "message" field
            JSONObject jsonObject = new JSONObject(jsonContent.toString());
            text.append(jsonObject.optString("message", "No message found."));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }

    private String analyzeSentiment(String text) {
        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        double sentimentScore = 0.0;
        int sentenceCount = 0;

        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            String sentimentClass = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            System.out.println("Sentence: \"" + sentence + "\" | Sentiment: " + sentimentClass);

            sentimentScore += getSentimentScore(sentimentClass);
            sentenceCount++;
        }

        sentimentScore = sentenceCount > 0 ? sentimentScore / sentenceCount : 2.0; // Default to Neutral for empty text

        if (sentimentScore >= 3) {
            return "Positive";
        } else if (sentimentScore <= 1) {
            return "Negative";
        } else {
            return "Neutral";
        }
    }

    private int getSentimentScore(String sentiment) {
        switch (sentiment) {
            case "Very Positive":
                return 4; // Higher weight
            case "Positive":
                return 3;
            case "Neutral":
                return 2;
            case "Negative":
                return 1;
            case "Very Negative":
                return 0;
            default:
                return 2;  // Default to Neutral
        }
    }
}
