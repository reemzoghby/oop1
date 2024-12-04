package com.mycompany.sentimentanalysisapp_rimzoghby;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONObject;

import java.io.*;

public class FileParser {

    public String parseFile(String filePath) {
        File file = new File(filePath);
        String extension = getFileExtension(file);

        switch (extension.toLowerCase()) {
            case "docx":
                return parseWord(filePath);
            case "pdf":
                return parsePDF(filePath);
            case "json":
                return parseJSON(filePath);
            default:
                throw new UnsupportedOperationException("Unsupported file type: " + extension);
        }
    }

    private String parseWord(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument document = new XWPFDocument(fis)) {
            StringBuilder text = new StringBuilder();
            document.getParagraphs().forEach(p -> text.append(p.getText()).append("\n"));
            return text.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error reading Word file: " + e.getMessage(), e);
        }
    }

    private String parsePDF(String filePath) {
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            return new PDFTextStripper().getText(document);
        } catch (IOException e) {
            throw new RuntimeException("Error reading PDF file: " + e.getMessage(), e);
        }
    }

    private String parseJSON(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            JSONObject jsonObject = new JSONObject(jsonContent.toString());
            return jsonObject.optString("message", "No message found.");
        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON file: " + e.getMessage(), e);
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int dotIndex = name.lastIndexOf(".");
        return dotIndex > 0 ? name.substring(dotIndex + 1) : "";
    }
}
