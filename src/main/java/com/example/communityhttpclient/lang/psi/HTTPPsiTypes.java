package com.example.communityhttpclient.lang.psi;

import com.intellij.psi.tree.IElementType;
import com.example.communityhttpclient.lang.HTTPLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

// These are for AST nodes, distinct from lexer tokens.
// In a simple language, they might map closely.
public interface HTTPPsiTypes {
    IElementType REQUEST = new HTTPElementType("REQUEST_RULE"); // Example rule
    IElementType HEADER_FIELD = new HTTPElementType("HEADER_FIELD_RULE"); // Example rule
    IElementType URI_SPEC = new HTTPElementType("URI_SPEC_RULE"); // Example rule
    IElementType BODY_RULE = new HTTPElementType("BODY_RULE"); // Example rule

    // Reusing HTTPElementType from HTTPTokenTypes for simplicity here
    class HTTPElementType extends IElementType {
        public HTTPElementType(@NotNull @NonNls String debugName) {
            super(debugName, HTTPLanguage.INSTANCE);
        }
    }
}
