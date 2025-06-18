package com.example.communityhttpclient.lang.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.example.communityhttpclient.lang.HTTPFileType;
import com.example.communityhttpclient.lang.HTTPLanguage;
import org.jetbrains.annotations.NotNull;

public class HTTPFile extends PsiFileBase {
    public HTTPFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, HTTPLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return HTTPFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "HTTP Request File";
    }
}
