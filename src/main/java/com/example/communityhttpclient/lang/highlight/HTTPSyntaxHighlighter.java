package com.example.communityhttpclient.lang.highlight;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.example.communityhttpclient.lang.lexer.HTTPLexerAdapter;
import com.example.communityhttpclient.lang.psi.HTTPTokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class HTTPSyntaxHighlighter extends SyntaxHighlighterBase {

    // Define TextAttributeKeys for different token types
    public static final TextAttributesKey METHOD_KEY = TextAttributesKey.createTextAttributesKey("HTTP_METHOD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey URL_KEY = TextAttributesKey.createTextAttributesKey("HTTP_URL", DefaultLanguageHighlighterColors.STRING); // Re-using string for now
    public static final TextAttributesKey HEADER_NAME_KEY = TextAttributesKey.createTextAttributesKey("HTTP_HEADER_NAME", DefaultLanguageHighlighterColors.METADATA);
    public static final TextAttributesKey HEADER_VALUE_KEY = TextAttributesKey.createTextAttributesKey("HTTP_HEADER_VALUE", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey COMMENT_KEY = TextAttributesKey.createTextAttributesKey("HTTP_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey REQUEST_SEPARATOR_KEY = TextAttributesKey.createTextAttributesKey("HTTP_REQUEST_SEPARATOR", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey BODY_KEY = TextAttributesKey.createTextAttributesKey("HTTP_BODY", DefaultLanguageHighlighterColors.DOC_COMMENT); // Example
    public static final TextAttributesKey BAD_CHAR_KEY = TextAttributesKey.createTextAttributesKey("HTTP_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

    private static final Map<IElementType, TextAttributesKey> TOKEN_HIGHLIGHTS = new HashMap<>();

    static {
        TOKEN_HIGHLIGHTS.put(HTTPTokenTypes.METHOD, METHOD_KEY);
        TOKEN_HIGHLIGHTS.put(HTTPTokenTypes.URL, URL_KEY);
        TOKEN_HIGHLIGHTS.put(HTTPTokenTypes.PROTOCOL_VERSION, URL_KEY); // Group with URL for now
        TOKEN_HIGHLIGHTS.put(HTTPTokenTypes.HEADER_NAME, HEADER_NAME_KEY);
        TOKEN_HIGHLIGHTS.put(HTTPTokenTypes.COLON, HEADER_NAME_KEY); // Color colon like header name
        TOKEN_HIGHLIGHTS.put(HTTPTokenTypes.HEADER_VALUE, HEADER_VALUE_KEY);
        TOKEN_HIGHLIGHTS.put(HTTPTokenTypes.COMMENT, COMMENT_KEY);
        TOKEN_HIGHLIGHTS.put(HTTPTokenTypes.REQUEST_SEPARATOR, REQUEST_SEPARATOR_KEY);
        TOKEN_HIGHLIGHTS.put(HTTPTokenTypes.BODY, BODY_KEY);
        TOKEN_HIGHLIGHTS.put(HTTPTokenTypes.BAD_CHARACTER, BAD_CHAR_KEY);
        TOKEN_HIGHLIGHTS.put(HTTPTokenTypes.CRLF, TextAttributesKey.createTextAttributesKey("HTTP_CRLF")); // No specific highlight by default
        TOKEN_HIGHLIGHTS.put(HTTPTokenTypes.WHITESPACE, TextAttributesKey.createTextAttributesKey("HTTP_WHITESPACE")); // No specific highlight
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new HTTPLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        return SyntaxHighlighterBase.pack(TOKEN_HIGHLIGHTS.get(tokenType));
    }
}
