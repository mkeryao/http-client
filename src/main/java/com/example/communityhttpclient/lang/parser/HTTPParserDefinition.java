package com.example.communityhttpclient.lang.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.example.communityhttpclient.lang.HTTPLanguage;
import com.example.communityhttpclient.lang.lexer.HTTPLexerAdapter;
import com.example.communityhttpclient.lang.psi.HTTPFile;
import com.example.communityhttpclient.lang.psi.HTTPTokenTypes;
// Import HTTPPsiTypes if you have specific PSI node types to create, otherwise use tokens for simple cases
// import com.example.communityhttpclient.lang.psi.HTTPPsiTypes;
import org.jetbrains.annotations.NotNull;

public class HTTPParserDefinition implements ParserDefinition {

    public static final IFileElementType FILE = new IFileElementType(HTTPLanguage.INSTANCE);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new HTTPLexerAdapter();
    }

    @NotNull
    @Override
    public PsiParser createParser(Project project) {
        return new HTTPParser();
    }

    @NotNull
    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return TokenSet.create(HTTPTokenTypes.COMMENT);
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY; // No string literals defined for now
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        // This is where you would map ASTNode types to your PSI element implementations
        // For now, we'll use a generic way or specific token types if they are also element types
        // e.g. if (node.getElementType() == HTTPPsiTypes.REQUEST) return new HTTPRequestImpl(node);
        // For the skeleton, often com.intellij.psi.impl.source.tree.CompositePsiElement is used by default
        // or you can use IElementType.createPsiElement(node) if the IElementType itself knows how to create it.
        // For a basic setup, often just using the default ASTWrapperPsiElement works for many tokens.
        // We'll rely on IntelliJ's default for non-overridden types for now.
        // To make specific PSI elements, you'd do:
        // if (node.getElementType() == HTTPPsiTypes.REQUEST) {
        //    return new HTTPRequestPsiElement(node); // Assuming you create this class
        // }
        return HTTPTokenTypes.HTTPElementType.createPsiElement(node); // Fallback for basic leaf nodes
                                                                // This requires HTTPElementType to be able to create Psi
                                                                // A better approach is to have specific Psi Element classes
                                                                // and use a generated com.example.communityhttpclient.lang.psi.impl.GeneratedFactory
                                                                // which is typical with GrammarKit.
                                                                // For a manual setup, you'd have a big if-else here.
    }

    @NotNull
    @Override
    public PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new HTTPFile(viewProvider);
    }

    // Optional: Define whitespace and comment tokens if they should be skipped by the parser in some contexts
    // (though comments are already handled by getCommentTokens for other features like commenting).
    // @NotNull
    // @Override
    // public TokenSet getWhitespaceTokens() {
    //     return TokenSet.create(HTTPTokenTypes.WHITESPACE, HTTPTokenTypes.CRLF);
    // }
}
