package com.example.communityhttpclient.lang;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class HTTPFileType extends LanguageFileType {
    public static final HTTPFileType INSTANCE = new HTTPFileType();

    private HTTPFileType() {
        super(HTTPLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "HTTP Request File";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "HTTP request file (.http, .rest)";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "http";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        // You'll want to create an icon for your file type (e.g., a 16x16 PNG/SVG)
        // For now, returning null or a default icon.
        // Consider using AllIcons.FileTypes.Text or creating your own.
        return null; // Replace with actual icon
    }
}
