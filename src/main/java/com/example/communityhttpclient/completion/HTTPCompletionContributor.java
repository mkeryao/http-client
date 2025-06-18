package com.example.communityhttpclient.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.openapi.editor.Editor;
// import com.intellij.openapi.util.TextRange; // Not used in this simplified version
import com.intellij.util.ProcessingContext;
import com.example.communityhttpclient.lang.HTTPLanguage;
import com.example.communityhttpclient.lang.psi.HTTPFile;
// Add HTTPTokenTypes if needed by a more specific pattern in future
// import com.example.communityhttpclient.lang.psi.HTTPTokenTypes;
import com.intellij.patterns.PatternCondition; // Required for custom conditions
import org.jetbrains.annotations.NotNull;

public class HTTPCompletionContributor extends CompletionContributor {

    public HTTPCompletionContributor() {
        // Provider for HTTP Methods
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement()
                        .withLanguage(HTTPLanguage.INSTANCE)
                        .inside(HTTPFile.class), // Basic pattern: trigger inside any HTTPFile
                                                 // A more specific pattern would be added here later e.g. .atStartOfRequest()
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet resultSet) {
                        // Basic heuristic to check if we are likely at a position for a method
                        Editor editor = parameters.getEditor();
                        int offset = parameters.getOffset();
                        String documentText = editor.getDocument().getText();
                        int lineStartOffset = offset;
                        while (lineStartOffset > 0 && documentText.charAt(lineStartOffset - 1) != '\n') {
                            lineStartOffset--;
                        }
                        String currentLinePrefix = documentText.substring(lineStartOffset, offset).trim();
                        // Only complete methods if the line is empty or looks like it's starting a word
                        if (!currentLinePrefix.isEmpty() && currentLinePrefix.contains(" ")) {
                            return; // If there's already a space, probably not a method start
                        }
                        // Avoid completing methods if line starts with #, //, or contains : (likely header or body)
                        String currentLineFull = documentText.substring(lineStartOffset,
                            documentText.indexOf('\n', lineStartOffset) == -1 ? documentText.length() : documentText.indexOf('\n', lineStartOffset));
                        if (currentLineFull.trim().startsWith("#") || currentLineFull.trim().startsWith("//") || currentLineFull.contains(":")) {
                            return;
                        }


                        String[] methods = {"GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS"};
                        for (String method : methods) {
                            resultSet.addElement(LookupElementBuilder.create(method).withBoldness(true));
                        }
                    }
                }
        );

        // Provider for HTTP Header Names
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement()
                        .withLanguage(HTTPLanguage.INSTANCE)
                        .inside(HTTPFile.class), // General pattern, refined by checks in the provider
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet resultSet) {
                        Editor editor = parameters.getEditor();
                        int offset = parameters.getOffset();
                        String documentText = editor.getDocument().getText();

                        int lineStartOffset = offset;
                        while (lineStartOffset > 0 && documentText.charAt(lineStartOffset - 1) != '\n') {
                            lineStartOffset--;
                        }

                        // Heuristic: Don't offer headers if it's the first line and it looks like a method
                        if (lineStartOffset == 0) {
                            String currentWord = getCurrentWord(documentText, offset).toUpperCase();
                            String[] methods = {"GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS"};
                            boolean isMethodPosition = false;
                            for(String m : methods) {
                                if (m.startsWith(currentWord)) {
                                    isMethodPosition = true;
                                    break;
                                }
                            }
                            if(isMethodPosition && !documentText.substring(0,offset).contains("\n")) return;
                        }

                        String lineContentBeforeCaret = documentText.substring(lineStartOffset, offset);
                        if (lineContentBeforeCaret.contains(":")) { // Already has a colon, likely a header value
                            return;
                        }
                        if (lineContentBeforeCaret.trim().startsWith("###") ||
                            lineContentBeforeCaret.trim().startsWith("#") ||
                            lineContentBeforeCaret.trim().startsWith("//")) { // Comment or separator line
                            return;
                        }

                        // Check if the *previous* line looks like it could have been a request line or empty
                        // This is to avoid suggesting headers in the middle of a request body without a blank line.
                        if (lineStartOffset > 0) {
                            int prevLineEnd = lineStartOffset -1;
                            int prevLineStart = prevLineEnd;
                            while(prevLineStart > 0 && documentText.charAt(prevLineStart-1) != '\n') {
                                prevLineStart--;
                            }
                            String prevLineText = documentText.substring(prevLineStart, prevLineEnd).trim();
                            if (!prevLineText.isEmpty() && !prevLineText.matches("^(GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS|TRACE|CONNECT)\\s+.*") && !prevLineText.startsWith("HTTP/")) {
                                // If previous line is not empty and not a request line, don't suggest headers.
                                // This is a very basic check. A proper PSI tree would identify the request block.
                                // return; // Disabled for now to be more permissive due to weak PSI
                            }
                        }


                        String[] commonHeaders = {
                                "Accept", "Accept-Charset", "Accept-Encoding", "Accept-Language",
                                "Authorization", "Cache-Control", "Connection", "Content-Length",
                                "Content-Type", "Cookie", "Date", "Expect", "From", "Host",
                                "If-Match", "If-Modified-Since", "If-None-Match", "If-Range",
                                "If-Unmodified-Since", "Max-Forwards", "Origin", "Pragma",
                                "Proxy-Authorization", "Range", "Referer", "TE", "Upgrade",
                                "User-Agent", "Via", "Warning",
                                "X-Requested-With", "X-Forwarded-For", "X-Forwarded-Proto",
                                "X-Http-Method-Override", "X-CSRF-Token", "DNT"
                        };
                        for (String header : commonHeaders) {
                            resultSet.addElement(LookupElementBuilder.create(header));
                        }
                    }
                }
        );

        // Provider for HTTP Protocol Versions
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement()
                        .withLanguage(HTTPLanguage.INSTANCE)
                        .inside(HTTPFile.class)
                        .with(new ProtocolVersionCompletionPatternCondition()),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet resultSet) {
                        resultSet.addElement(LookupElementBuilder.create("HTTP/1.1"));
                        resultSet.addElement(LookupElementBuilder.create("HTTP/2"));
                    }
                }
        );
    }

    // Helper method (can be static or in a utility class)
    private static String getCurrentWord(String text, int offset) {
        if (offset > text.length()) offset = text.length();
        if (offset > 0 && !Character.isJavaIdentifierPart(text.charAt(offset-1))) { // If caret is after a non-identifier char
             if (offset < text.length() && Character.isJavaIdentifierPart(text.charAt(offset))) {
                 // and before an identifier char, means we are at the start of a new word.
             } else {
                 return ""; // Not at a word boundary relevant for starting a new word
             }
        }


        int start = offset - 1;
        while (start >= 0 && Character.isJavaIdentifierPart(text.charAt(start))) {
            start--;
        }
        start++; // move back to the start of the word

        int end = offset;
        while (end < text.length() && Character.isJavaIdentifierPart(text.charAt(end))) {
            end++;
        }
        if (start < end) { // Ensure start is less than end to avoid StringIndexOutOfBounds
            return text.substring(start, end);
        }
        return "";
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        // This method can be used for more dynamic contributions or to override default behavior.
        // For most cases, using extend() in the constructor is preferred for registering pattern-based providers.
        super.fillCompletionVariants(parameters, result);
    }

    // Optional: Override other methods if needed, e.g., beforeCompletion()

    static class ProtocolVersionCompletionPatternCondition extends PatternCondition<PsiElement> {
        public ProtocolVersionCompletionPatternCondition() {
            super("isHttpProtocolVersionPosition");
        }

        @Override
        public boolean accepts(@NotNull PsiElement psiElement, ProcessingContext context) {
            if (psiElement == null) return false;

            PsiFile containingFile = psiElement.getContainingFile();
            if (!(containingFile instanceof HTTPFile)) return false;

            // Try to get CompletionParameters from context to fetch the editor
            Object paramsObj = context.get(CompletionUtilCore.COMPLETION_PARAMETERS);
            if (!(paramsObj instanceof CompletionParameters)) return false;
            CompletionParameters parameters = (CompletionParameters) paramsObj;

            Editor editor = parameters.getEditor();
            // Fallback if not directly in parameters (though it should be)
            // if (editor == null) {
            //    editor = CompletionUtil.getEditorFromCompletionParameters(parameters);
            // }
            if (editor == null) return false;

            int offset = editor.getCaretModel().getOffset();
            String documentText = editor.getDocument().getText();
            if (offset > documentText.length()) return false;

            int lineStartOffset = offset;
            while (lineStartOffset > 0 && documentText.charAt(lineStartOffset - 1) != '\n') {
                lineStartOffset--;
            }

            if (lineStartOffset > 0) {
                int prevLineEnd = lineStartOffset -1;
                int prevLineActualStart = prevLineEnd;
                if (prevLineActualStart > 0) { // Check if there is content before this potential prev line
                     while(prevLineActualStart > 0 && documentText.charAt(prevLineActualStart -1) != '\n') {
                        prevLineActualStart --;
                    }
                } else { // prevLineEnd was at char 0, so prevLineActualStart is 0
                    prevLineActualStart = 0;
                }
                String prevLineText = documentText.substring(prevLineActualStart, prevLineEnd).trim();
                if (!prevLineText.isEmpty() && !prevLineText.equals("###")) {
                    return false;
                }
            }

            String currentLineToCaret = documentText.substring(lineStartOffset, offset);
            // String currentLineFull = documentText.substring(lineStartOffset,
            //    documentText.indexOf("\n", lineStartOffset) == -1 ? documentText.length() : documentText.indexOf("\n", lineStartOffset));

            String[] partsOnLineToCaret = currentLineToCaret.trim().split("\\s+");
            if (partsOnLineToCaret.length < 2 || partsOnLineToCaret[0].isEmpty()) {
                return false;
            }

            if (!partsOnLineToCaret[0].matches("GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS|TRACE|CONNECT")) {
                return false;
            }

            String textBeforeCaret = "";
            if (offset > lineStartOffset) {
                 int wordStart = offset -1;
                 while(wordStart >= lineStartOffset && wordStart < documentText.length() && !Character.isWhitespace(documentText.charAt(wordStart))) {
                     wordStart--;
                 }
                 wordStart++; // Back to the start of the potential word
                 if (wordStart < offset) { // If wordStart is valid and before offset
                    textBeforeCaret = documentText.substring(wordStart, offset);
                 } else if (offset > 0 && Character.isWhitespace(documentText.charAt(offset-1))) {
                     textBeforeCaret = ""; // Caret is after a space
                 } else {
                     // Caret is not in a position to start a new word like "HTTP/"
                     // e.g. METHOD /someU|RL - here it's part of URL
                     if (partsOnLineToCaret.length < 3 && !Character.isWhitespace(documentText.charAt(offset-1))) return false;
                 }
            }


            // If we are at "METHOD URL |" (caret after space) -> textBeforeCaret is empty
            // If we are at "METHOD URL H|" -> textBeforeCaret is "H"
            if (textBeforeCaret.isEmpty() || "HTTP/".startsWith(textBeforeCaret.toUpperCase())) {
                // Check if we are likely in the third position on the line
                if (partsOnLineToCaret.length == 2 && textBeforeCaret.isEmpty() && offset > 0 && Character.isWhitespace(documentText.charAt(offset-1))) {
                    return true; // After "METHOD URL "
                }
                if (partsOnLineToCaret.length == 3 && "HTTP/".startsWith(partsOnLineToCaret[2].toUpperCase()) && textBeforeCaret.equalsIgnoreCase(partsOnLineToCaret[2])) {
                     return true; // Typing the third part, and it looks like HTTP/
                }
                // If the current word being typed starts with H, HT, HTT, HTTP, HTTP/
                 if ("HTTP/".startsWith(textBeforeCaret.toUpperCase()) && partsOnLineToCaret.length >= 2) {
                    // Ensure that the second part (URL) is somewhat substantial, not just e.g. "GET / H"
                    if (partsOnLineToCaret.length > 1 && partsOnLineToCaret[1].length() > 0) {
                        return true;
                    }
                 }
            }

            return false;
        }
    }
}
