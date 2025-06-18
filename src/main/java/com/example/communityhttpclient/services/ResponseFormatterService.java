package com.example.communityhttpclient.services;

// Imports for GSON
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

// Imports for XML
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

// (Optional: JSoup for HTML)
// import org.jsoup.Jsoup;
// import org.jsoup.nodes.Document;

public class ResponseFormatterService {

    public String format(String rawBody, String fullContentType) {
        if (rawBody == null || rawBody.isEmpty()) {
            return ""; // Or handle as appropriate
        }
        if (fullContentType == null || fullContentType.isEmpty()) {
            return rawBody; // Treat as plain text if no content type
        }

        String mainContentType = fullContentType;
        if (fullContentType.contains(";")) {
            mainContentType = fullContentType.substring(0, fullContentType.indexOf(';')).trim();
        }
        mainContentType = mainContentType.toLowerCase();

        // Handle common binary types first by not attempting to format
        if (mainContentType.startsWith("image/") ||
            mainContentType.startsWith("audio/") ||
            mainContentType.startsWith("video/") ||
            mainContentType.equals("application/octet-stream") ||
            mainContentType.equals("application/pdf") ||
            mainContentType.equals("application/zip")) {
            return "[Binary content (" + mainContentType + ") - Not displayable as text. Length: " + rawBody.length() + " bytes]";
        }


        if (mainContentType.endsWith("+json") || mainContentType.equals("application/json")) {
            return prettyPrintJson(rawBody);
        } else if (mainContentType.endsWith("+xml") || mainContentType.equals("application/xml") || mainContentType.equals("text/xml")) {
            return prettyPrintXml(rawBody);
        } else if (mainContentType.equals("text/html")) {
            // return prettyPrintHtml(rawBody); // Optional: if you implement HTML pretty printing
            return rawBody; // For now, return HTML raw, highlighting will be done by EditorTextField
        } else if (mainContentType.startsWith("text/")) {
            return rawBody; // For other text/* types, return raw
        }

        if (isLikelyText(rawBody)) {
             return rawBody;
        } else {
            return "[Unrecognized content type ("+mainContentType+") or potential binary data. Length: " + rawBody.length() + " bytes]";
        }
    }

    private String prettyPrintJson(String rawJson) {
        try {
            JsonElement jsonElement = JsonParser.parseString(rawJson);
            Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
            return gson.toJson(jsonElement);
        } catch (JsonSyntaxException e) {
            // In a real plugin, use IntelliJ's logging:
            // com.intellij.openapi.diagnostic.Logger.getInstance(ResponseFormatterService.class).warn("Failed to parse JSON", e);
            return rawJson;
        }
    }

    private String prettyPrintXml(String rawXml) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            try {
                transformerFactory.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, true);
            } catch (TransformerConfigurationException e) {
                // com.intellij.openapi.diagnostic.Logger.getInstance(ResponseFormatterService.class).warn("Secure processing not supported by TransformerFactory", e);
            }

            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            StreamResult result = new StreamResult(new StringWriter());
            Source source = new StreamSource(new StringReader(rawXml));
            transformer.transform(source, result);
            return result.getWriter().toString();
        } catch (TransformerException e) {
            // com.intellij.openapi.diagnostic.Logger.getInstance(ResponseFormatterService.class).warn("Failed to format XML", e);
            return rawXml;
        }
    }

    // Optional: HTML pretty printing using Jsoup (if added as a dependency)
    /*
    private String prettyPrintHtml(String rawHtml) {
        try {
            // org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(rawHtml);
            // doc.outputSettings().indentAmount(2);
            // return doc.outerHtml();
            return rawHtml; // Placeholder
        } catch (Exception e) {
            return rawHtml;
        }
    }
    */

    private boolean isLikelyText(String content) {
        if (content == null || content.isEmpty()) return true;
        long nonPrintableCount = 0;
        int checkLength = Math.min(content.length(), 1024);

        for (int i = 0; i < checkLength; i++) {
            char c = content.charAt(i);
            if (!Character.isWhitespace(c) && (Character.isISOControl(c) || !Character.isDefined(c))) {
                if (c != '\t' && c != '\n' && c != '\r') {
                    nonPrintableCount++;
                }
            }
        }
        return (double)nonPrintableCount / checkLength < 0.10;
    }
}
