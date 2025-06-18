package com.example.communityhttpclient.lang;

import com.intellij.lang.Language;

public class HTTPLanguage extends Language {
    public static final HTTPLanguage INSTANCE = new HTTPLanguage();

    private HTTPLanguage() {
        super("HTTP");
    }
}
