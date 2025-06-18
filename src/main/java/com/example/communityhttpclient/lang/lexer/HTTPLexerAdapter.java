package com.example.communityhttpclient.lang.lexer;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import com.example.communityhttpclient.lang.psi.HTTPTokenTypes;
import org.jetbrains.annotations.Nullable;

// For a real plugin, you would typically generate a lexer using JFlex
// (see _HTTPLexer.flex file and the JFlex task in build.gradle).
// This is a very simplified placeholder to provide the basic structure.
public class HTTPLexerAdapter extends LexerBase {

    private CharSequence buffer;
    private int startOffset;
    private int endOffset;
    private int currentOffset;
    private IElementType currentTokenType;

    // Simple states for our manual lexer
    private static final int STATE_INITIAL = 0;
    private static final int STATE_IN_HEADER_NAME = 1;
    private static final int STATE_IN_HEADER_VALUE = 2;
    private static final int STATE_IN_BODY = 3;
    private static final int STATE_IN_URL = 4; // Simplified

    private int currentState = STATE_INITIAL;


    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.currentOffset = startOffset;
        this.currentState = initialState == 0 ? STATE_INITIAL : initialState; // Allow initial state override if needed
        advance(); // Find the first token
    }

    @Override
    public int getState() {
        // In a more complex lexer, this would return the current JFlex state
        return currentState;
    }

    @Nullable
    @Override
    public IElementType getTokenType() {
        return currentTokenType;
    }

    @Override
    public int getTokenStart() {
        return startOffset;
    }

    @Override
    public int getTokenEnd() {
        return currentOffset;
    }

    @Override
    public void advance() {
        if (currentOffset >= endOffset) {
            currentTokenType = null;
            return;
        }

        startOffset = currentOffset;
        char currentChar = buffer.charAt(currentOffset);

        // This is a highly simplified tokenizer, not a robust HTTP parser.
        // It's just to get some basic tokens.
        if (Character.isWhitespace(currentChar)) {
            currentOffset++;
            while (currentOffset < endOffset && Character.isWhitespace(buffer.charAt(currentOffset))) {
                currentOffset++;
            }
            currentTokenType = HTTPTokenTypes.WHITESPACE;
        } else if (currentChar == '#' && currentOffset + 2 < endOffset && buffer.charAt(currentOffset+1) == '#' && buffer.charAt(currentOffset+2) == '#') {
            currentOffset += 3;
            currentTokenType = HTTPTokenTypes.REQUEST_SEPARATOR;
        } else if (currentChar == '#') {
             currentOffset++;
            while (currentOffset < endOffset && buffer.charAt(currentOffset) != '\n') {
                currentOffset++;
            }
            currentTokenType = HTTPTokenTypes.COMMENT;
        } else if (isHttpMethodStart(currentOffset)) { // GET, POST, etc.
            int tokenEnd = currentOffset;
            while (tokenEnd < endOffset && Character.isLetter(buffer.charAt(tokenEnd))) {
                tokenEnd++;
            }
            // Basic check, real methods are specific
            String potentialMethod = buffer.subSequence(currentOffset, tokenEnd).toString().toUpperCase();
            if (potentialMethod.equals("GET") || potentialMethod.equals("POST") || potentialMethod.equals("PUT") || potentialMethod.equals("DELETE") || potentialMethod.equals("PATCH") || potentialMethod.equals("HEAD") || potentialMethod.equals("OPTIONS")) {
               currentOffset = tokenEnd;
               currentTokenType = HTTPTokenTypes.METHOD;
            } else { // Fallback for simplicity
               currentTokenType = advanceAsSimpleWord();
            }

        } else if (currentChar == '/' || (Character.isLetter(currentChar) && buffer.subSequence(currentOffset, Math.min(endOffset, currentOffset + 4)).toString().toLowerCase().startsWith("http"))) {
            // Very basic URL detection
            while(currentOffset < endOffset && !Character.isWhitespace(buffer.charAt(currentOffset)) && buffer.charAt(currentOffset) != '\n'){
                currentOffset++;
            }
            currentTokenType = HTTPTokenTypes.URL;
        }
        // Simplified: Assume anything after a method and URL on the same line, before a colon, is a header name.
        // This is not robust.
        else if (Character.isLetterOrDigit(currentChar) && (lookBehindForMethodOrUrlOnLine())) {
            int colonPos = findNext(currentOffset, ':');
            if (colonPos != -1) {
                boolean hasSpaceAfter = colonPos + 1 < endOffset && buffer.charAt(colonPos+1) == ' ';
                int valueStart = hasSpaceAfter ? colonPos + 2 : colonPos + 1;

                // Check if what's before colon is a valid header name (no spaces etc.)
                boolean isValidHeaderName = true;
                for(int i=startOffset; i < colonPos; i++) {
                    if(Character.isWhitespace(buffer.charAt(i))) {
                        isValidHeaderName = false;
                        break;
                    }
                }

                if(isValidHeaderName) {
                    // We'll tokenize name, then colon, then value in subsequent calls if possible
                    // For now, just consume the name part for simplicity in this advance() call
                    currentOffset = colonPos;
                    currentTokenType = HTTPTokenTypes.HEADER_NAME;
                } else {
                   currentTokenType = advanceAsSimpleWord();
                }

            } else { // No colon, could be part of URL, body, or other things
                currentTokenType = advanceAsSimpleWord();
            }
        } else if (currentChar == ':') {
            currentOffset++;
            currentTokenType = HTTPTokenTypes.COLON;
        }
        // If we are past a colon and on the same line, it's likely a header value
        else if (lookBehindForColonOnLine()) {
            while (currentOffset < endOffset && buffer.charAt(currentOffset) != '\n') {
                currentOffset++;
            }
            currentTokenType = HTTPTokenTypes.HEADER_VALUE;
        }
        // Rudimentary body detection: if we encounter content after a blank line,
        // and not starting with ### or #. This is very basic.
        else if (isPotentiallyBody()) {
            while(currentOffset < endOffset) {
                // Stop if we hit a new request separator
                if (currentOffset + 2 < endOffset &&
                    buffer.charAt(currentOffset) == '#' &&
                    buffer.charAt(currentOffset+1) == '#' &&
                    buffer.charAt(currentOffset+2) == '#') {
                    break;
                }
                currentOffset++;
            }
            currentTokenType = HTTPTokenTypes.BODY;

        } else { // Default: consume one char as bad char or simple word
            currentTokenType = advanceAsSimpleWord();
        }
    }

    private IElementType advanceAsSimpleWord() {
        // Fallback: treat as a sequence of non-whitespace chars
        int tokenEnd = currentOffset;
        while (tokenEnd < endOffset && !Character.isWhitespace(buffer.charAt(tokenEnd))) {
            tokenEnd++;
        }
        if (tokenEnd > currentOffset) {
            currentOffset = tokenEnd;
            // Could be various things; refine later. For now, maybe URL or part of body.
            // Let's assume it's a generic part of the URL or an unclassified token.
            return HTTPTokenTypes.URL; // Placeholder, this needs refinement
        } else {
            currentOffset++;
            return HTTPTokenTypes.BAD_CHARACTER;
        }
    }


   private boolean isHttpMethodStart(int offset) {
       if (offset + 2 >= endOffset) return false; // Minimum length for GET
       // Check if it's at the beginning of a line or after ###
       int prevCharPos = offset -1;
       while(prevCharPos >=0 && Character.isWhitespace(buffer.charAt(prevCharPos)) && buffer.charAt(prevCharPos) != '\n') {
           prevCharPos--;
       }
       boolean atLineStart = (prevCharPos < 0 || buffer.charAt(prevCharPos) == '\n' || (prevCharPos >=2 && buffer.subSequence(prevCharPos-2, prevCharPos+1).toString().equals("###")));

       return atLineStart && Character.isLetter(buffer.charAt(offset));
   }

   private int findNext(int from, char c) {
       for (int i = from; i < endOffset; i++) {
           if (buffer.charAt(i) == c || buffer.charAt(i) == '\n') { // Stop at newline too
               return buffer.charAt(i) == c ? i : -1;
           }
       }
       return -1;
   }

   private boolean lookBehindForMethodOrUrlOnLine() {
       // Scan backwards on the current line from startOffset-1
       for (int i = startOffset - 1; i >= 0; i--) {
           if (buffer.charAt(i) == '\n') return false; // Reached previous line
           // Simple check, a more robust check would involve checking token types of previous tokens
           if (Character.isLetterOrDigit(buffer.charAt(i))) return true; // Found some text, assume it could be method/URL
       }
       return false; // Beginning of buffer or only whitespace before
   }

    private boolean lookBehindForColonOnLine() {
       for (int i = startOffset - 1; i >= 0; i--) {
           if (buffer.charAt(i) == '\n') return false;
           if (buffer.charAt(i) == ':') return true;
       }
       return false;
   }

   private boolean isPotentiallyBody() {
       // True if we are on a new line, and the line is not empty,
       // and it doesn't start with # or ###
       if (startOffset == 0) return false; // Cannot be body at the very start

       int prevMeaningfulChar = -1;
       int twoLinesBack = -1;
       int lines = 0;

       for(int i = startOffset -1; i >=0; i--) {
           if (buffer.charAt(i) == '\n') {
               lines++;
               if (lines == 1) prevMeaningfulChar = i -1; // char before the first

               if (lines == 2) {
                   twoLinesBack = i;
                   break;
               }
           } else if (Character.isWhitespace(buffer.charAt(i))) {
               continue;
           } else if (prevMeaningfulChar == -1) { // content on the same line before current token
                return false;
           }
       }

       // Check if the line before current was empty (all whitespace between two newlines, or from start to first newline)
       boolean prevLineWasBlank = true;
       int lineStartForPrev = (twoLinesBack == -1) ? 0 : twoLinesBack + 1;
       for(int i=lineStartForPrev; i < ( (prevMeaningfulChar == -1 || prevMeaningfulChar < lineStartForPrev )? startOffset -1 : prevMeaningfulChar +1) ; i++) {
            if (i >= endOffset) break; // Bounds check
            if (buffer.charAt(i) == '\n') break; // Reached the end of the previous line
            if (!Character.isWhitespace(buffer.charAt(i))) {
                prevLineWasBlank = false;
                break;
            }
       }
       if (!prevLineWasBlank) return false;


       // Current line should not start with # or ###
       if (buffer.charAt(startOffset) == '#') return false;

       return true; // If previous line was blank and current line is not a comment/separator
   }


    @NotNull
    @Override
    public CharSequence getBufferSequence() {
        return this.buffer;
    }

    @Override
    public int getBufferEnd() {
        return this.endOffset;
    }
}
