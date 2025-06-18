package com.example.communityhttpclient.lang.psi;

import com.intellij.psi.tree.IElementType;
import com.example.communityhttpclient.lang.HTTPLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class HTTPTokenTypes {
    public static final IElementType METHOD = new HTTPElementType("METHOD");
    public static final IElementType URL = new HTTPElementType("URL");
    public static final IElementType PROTOCOL_VERSION = new HTTPElementType("PROTOCOL_VERSION");

    public static final IElementType HEADER_NAME = new HTTPElementType("HEADER_NAME");
    public static final IElementType HEADER_VALUE = new HTTPElementType("HEADER_VALUE");
    public static final IElementType COLON = new HTTPElementType("COLON"); // For separating header name and value

    public static final IElementType REQUEST_SEPARATOR = new HTTPElementType("REQUEST_SEPARATOR"); // ###
    public static final IElementType COMMENT = new HTTPElementType("COMMENT"); // # or //
    public static final IElementType BODY = new HTTPElementType("BODY");
    public static final IElementType CRLF = new HTTPElementType("CRLF"); // Carriage Return Line Feed
    public static final IElementType WHITESPACE = new HTTPElementType("WHITESPACE");
    public static final IElementType BAD_CHARACTER = new HTTPElementType("BAD_CHARACTER");


    public static class HTTPElementType extends IElementType {
        public HTTPElementType(@NotNull @NonNls String debugName) {
            super(debugName, HTTPLanguage.INSTANCE);
        }
    }
}
