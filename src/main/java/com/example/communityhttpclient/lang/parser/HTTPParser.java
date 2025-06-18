package com.example.communityhttpclient.lang.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
// For a real parser, you would import your HTTPPsiTypes and HTTPTokenTypes
// import static com.example.communityhttpclient.lang.psi.HTTPPsiTypes.*;
// import static com.example.communityhttpclient.lang.psi.HTTPTokenTypes.*;

public class HTTPParser implements PsiParser {

    @NotNull
    @Override
    public ASTNode parse(@NotNull IElementType root, @NotNull PsiBuilder builder) {
        PsiBuilder.Marker rootMarker = builder.mark();

        // Simple parsing loop: consume all tokens.
        // A real parser would have recursive descent methods for each rule in your grammar.
        // e.g., parseRequest(builder), parseHeaders(builder), parseBody(builder)
        while (!builder.eof()) {
            // For now, just advance. In a real parser, you'd check token types
            // and build up the PSI tree with builder.mark().done(PSI_TYPE) or .error().
            // Example:
            // if (builder.getTokenType() == HTTPTokenTypes.METHOD) {
            //     PsiBuilder.Marker requestMarker = builder.mark();
            //     builder.advanceLexer(); // consume METHOD token
            //     // ... parse rest of request ...
            //     requestMarker.done(HTTPPsiTypes.REQUEST);
            // } else {
            //     builder.error("Expected HTTP method");
            //     builder.advanceLexer(); // consume the unexpected token to avoid infinite loop
            // }
            builder.advanceLexer();
        }

        rootMarker.done(root);
        return builder.getTreeBuilt();
    }
}
