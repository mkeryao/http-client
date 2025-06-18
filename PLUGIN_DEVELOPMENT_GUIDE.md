# Community HTTP Client Plugin - Development Guide

This guide outlines how to set up the "Community HTTP Client" plugin project in IntelliJ IDEA using the files generated so far, and lists the immediate next steps for development.

## 1. Project Setup Instructions

### a. Create a New IntelliJ Platform Plugin Project
1.  Open IntelliJ IDEA (Ultimate or Community with the plugin development features).
2.  Go to `File -> New -> Project...`.
3.  Select `IntelliJ Platform Plugin` from the project types on the left.
4.  Click `Next`.
5.  Give your project a name (e.g., `CommunityHttpClientPlugin`).
6.  Choose a suitable location.
7.  Select a **Project SDK** that is a valid IntelliJ IDEA installation (this will be used to build against). If you don't have one, you can add it via `Add SDK... -> IntelliJ Platform Plugin SDK`.
8.  Click `Finish`.

### b. Place Generated Java Files
The Java files you've created should be placed within the `src` (or `src/main/java`) directory of your new plugin project, maintaining their package structure:
*   `com/example/communityhttpclient/lang/HTTPLanguage.java`
*   `com/example/communityhttpclient/lang/HTTPFileType.java`
*   `com/example/communityhttpclient/lang/HTTPFileTypeFactory.java`
*   `com/example/communityhttpclient/lang/psi/HTTPTokenTypes.java`
*   `com/example/communityhttpclient/lang/psi/HTTPPsiTypes.java`
*   `com/example/communityhttpclient/lang/psi/HTTPFile.java`
*   `com/example/communityhttpclient/lang/lexer/HTTPLexerAdapter.java`
*   `com/example/communityhttpclient/lang/parser/HTTPParserDefinition.java`
*   `com/example/communityhttpclient/lang/parser/HTTPParser.java`
*   `com/example/communityhttpclient/lang/highlight/HTTPSyntaxHighlighter.java`
*   `com/example/communityhttpclient/lang/highlight/HTTPSyntaxHighlighterFactory.java`

So, for example, `HTTPLanguage.java` would be at `CommunityHttpClientPlugin/src/main/java/com/example/communityhttpclient/lang/HTTPLanguage.java`.

### c. Place `plugin.xml`
The `plugin.xml` file defines your plugin's extensions and metadata.
1.  The generated `plugin.xml` should be placed in a directory named `META-INF` under the project's `resources` directory. Typically, this path is `CommunityHttpClientPlugin/src/main/resources/META-INF/plugin.xml`.
    *   If your project structure doesn't have `src/main/resources` yet, you can create it. Right-click the `main` directory (or `src` if `main` doesn't exist) -> `New` -> `Directory`. Name it `resources`. Then create `META-INF` inside `resources`.

#### Adding a File Type Icon
*   **Importance**: A custom icon for your `.http` files enhances the plugin's usability and provides a clear visual identity within the IntelliJ IDEA interface, making it easier for users to identify these files.

*   **Icon Creation**:
    *   It's recommended to use an SVG icon for scalability and clarity. A common size is 16x16 pixels. For this plugin, you might name it `httpFile.svg` or similar.

*   **File Placement**:
    *   Place your icon file (e.g., `httpFile.svg`) into a dedicated `icons` directory within your `src/main/resources` folder. The typical path would be `src/main/resources/icons/httpFile.svg`.
    *   Ensure that `src/main/resources` is marked as a "Resources Root" in your IntelliJ IDEA project structure. This is usually configured automatically in Gradle-based projects.

*   **Loading the Icon**:
    *   The icon is loaded within your `HTTPFileType.java` class using `com.intellij.openapi.util.IconLoader`.
    *   The `IconLoader.getIcon()` method takes two arguments: the path to the icon file (relative to any resources root) and the class context (typically the class where the `Icon` field is declared).
    *   It's best practice to load the icon into a static final field to ensure it's loaded only once.

For example, in `HTTPFileType.java`:

```java
package com.example.communityhttpclient.lang; // Ensure your actual package

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader; // Required import
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon; // Required import

public class HTTPFileType extends LanguageFileType {
    public static final HTTPFileType INSTANCE = new HTTPFileType();

    // Load the icon once. Ensure the path /icons/httpFile.svg is correct
    // relative to your resources root.
    public static final Icon FILE_ICON = IconLoader.getIcon("/icons/httpFile.svg", HTTPFileType.class);

    private HTTPFileType() {
        super(HTTPLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "HTTP Request File"; // As defined before
    }

    @NotNull
    @Override
    public String getDescription() {
        return "HTTP request file (.http, .rest)"; // As defined before
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "http"; // As defined before
    }

    @Nullable
    @Override
    public Icon getIcon() {
        // Return the loaded icon
        return FILE_ICON;
    }
}
```
Make sure the path `/icons/httpFile.svg` correctly points to your icon's location within the resources directory.

## 2. Initial Build and Run

### a. Configure Plugin SDK (if not already done)
1.  Go to `File -> Project Structure...`.
2.  Under `Platform Settings`, select `SDKs`.
3.  Ensure you have an `IntelliJ Platform Plugin SDK` configured. If not, click the `+` button, select `IntelliJ Platform Plugin SDK`, and point it to a local installation of IntelliJ IDEA.
4.  Under `Project Settings`, select `Project`. Make sure the `Project SDK` is set to the IntelliJ Platform Plugin SDK you configured.

### b. Run the Plugin
1.  IntelliJ IDEA automatically creates a run configuration for plugins: `Run Plugin`.
2.  Select this configuration from the dropdown near the play button in the toolbar.
3.  Click the green play button (or `Shift+F10`).
4.  This will build your plugin and launch a new, sandboxed instance of IntelliJ IDEA with your plugin installed.
5.  In the sandbox instance, you can create a new file (e.g., `test.http` or `test.rest`). It should be recognized as "HTTP Request File" (due to `HTTPFileTypeFactory`), and you should see some basic syntax highlighting (due to `HTTPSyntaxHighlighter` and the rudimentary `HTTPLexerAdapter`).

## 3. Immediate Next Steps for the Developer

The current state of the plugin provides a very basic foundation. To make it functional, focus on these areas:

### a. Improve the Lexer (`HTTPLexerAdapter.java`)
*   **Problem:** The current `HTTPLexerAdapter` is a manual, highly simplified placeholder. It will struggle with complex HTTP syntax, edge cases, and performance.
*   **Solution:** The standard approach for IntelliJ plugins is to use **JFlex** to generate a lexer.
    1.  Create a JFlex definition file (e.g., `_HTTPLexer.flex`) that describes the lexical rules for your HTTP file format.
    2.  Integrate JFlex into your build process (usually via Gradle by adding the JFlex plugin and a task to generate the lexer Java class from the `.flex` file).
    3.  The generated Java class (e.g., `_HTTPLexer`) is then wrapped by `FlexAdapter` (similar to how `HTTPLexerAdapter` might use a `FlexAdapter` if it were wrapping a JFlex lexer, but typically you'd replace `HTTPLexerAdapter` with a new class like `HTTPLexer` that is just `public class HTTPLexer extends FlexAdapter { public HTTPLexer() { super(new _HTTPLexer(null)); } }`).
    4.  Update `HTTPParserDefinition.createLexer()` to return an instance of your new JFlex-based lexer.

### b. Implement the Parser (`HTTPParser.java`)
*   **Problem:** The current `HTTPParser.java` is a stub that consumes all tokens without building a structured Program Structure Interface (PSI) tree. Without a proper PSI tree, features like code navigation, refactoring, find usages, and deeper semantic analysis are impossible.
*   **Solution:**
    1.  **Define a Grammar:** Formally define the grammar of your `.http` files (e.g., what constitutes a request, headers, body, comments, separators).
    2.  **Recursive Descent Parsing:** Implement the parser using IntelliJ's `PsiBuilder`. This involves writing methods for each rule in your grammar (e.g., `parseRequest()`, `parseHeaderField()`, `parseBody()`). These methods will use `builder.mark()` to create nodes in the PSI tree, `builder.advanceLexer()` to consume tokens, and `marker.done(PSI_ELEMENT_TYPE)` to complete a node or `marker.error("message")` for syntax errors.
    3.  **Grammar-Kit (Recommended):** For more complex languages, consider using [Grammar-Kit](https.github.com/JetBrains/Grammar-Kit), an IntelliJ plugin that helps you generate a parser and PSI element classes from a `.bnf` (Backus-Naur Form) grammar file. This significantly speeds up development and reduces boilerplate.

### c. Refine Token and PSI Types (`HTTPTokenTypes.java`, `HTTPPsiTypes.java`)
*   As you define your grammar and implement the parser, you will need to:
    *   Add more specific token types to `HTTPTokenTypes.java` (e.g., for different parts of a URL, specific keywords).
    *   Define more PSI element types in `HTTPPsiTypes.java` that correspond to the non-terminal symbols in your grammar (e.g., `REQUEST`, `METHOD_LINE`, `HEADER_BLOCK`, `JSON_BODY`). These represent the nodes in your PSI tree.

### d. Create PSI Element Implementations
*   **Problem:** The current `HTTPParserDefinition.createElement()` uses a fallback. For a real PSI tree, you need custom classes for your PSI elements.
*   **Solution:** For each significant element type defined in `HTTPPsiTypes` (e.g., `REQUEST`, `HEADER_FIELD`), create a corresponding Java class that implements `PsiElement` (often by extending a base class like `ASTWrapperPsiElement` or more specific ones if using generated code from Grammar-Kit).
    *   Example: `public class HTTPRequestImpl extends ASTWrapperPsiElement implements HTTPRequest { ... }` (where `HTTPRequest` might be an interface).
    *   These classes allow you to add methods for navigating the PSI tree (e.g., `getRequestUrl()`, `getHeaders()`) and provide other language-specific logic.
    *   Update `HTTPParserDefinition.createElement()` to instantiate these custom PSI element classes based on the `ASTNode`'s element type. If using Grammar-Kit, it often generates a factory class for this.

### e. Customize Editor Colors & Fonts
*   The `HTTPSyntaxHighlighter.java` defines several `TextAttributesKey`s (e.g., `METHOD_KEY`, `URL_KEY`).
*   To allow users to customize these:
    1.  Create a `ColorSettingsPage` implementation (extension point `com.intellij.colorSettingsPage`).
    2.  This class will list your `TextAttributesKey`s, provide display names for them in the "Settings/Preferences -> Editor -> Color Scheme" dialog, and show a demo text that previews the highlighting.

By tackling these steps, you'll move from a basic file type recognition and highlighting to a more feature-rich language plugin. The lexer and parser are the most critical next steps. Good luck!

### Using Environment Variables
*   **Purpose**: Environment files allow you to manage different sets of variables for various environments, such as development, staging, and production. This is crucial for handling API endpoints, authentication tokens, and other configuration parameters that change between these stages. The primary files are `http-client.env.json` (for shareable, non-sensitive variables) and `http-client.private.env.json` (for sensitive or local override variables).

*   **File Structure**:
    *   Both environment files use a JSON structure. The top-level element is a JSON object where each key represents an environment name (e.g., "development", "production"). The value associated with each environment name is another JSON object containing key-value pairs for the variables in that environment.
    *   `http-client.private.env.json` follows the exact same structure. If both files define the same environment and variable, the value from the private file takes precedence. This is useful for keeping API keys or other secrets out of version control (by adding `http-client.private.env.json` to `.gitignore`).
    *   These files are typically expected to be in the root directory of your project.

*   **Variable Syntax in `.http` files**:
    *   To use a variable within your `.http` request files, use the double curly brace syntax: `{{variable_name}}`.
    *   This syntax can be used in URLs (e.g., `GET {{host}}/users`), header values (e.g., `X-Auth-Token: {{api_key}}`), and within the request body (e.g., `{"username": "{{default_user}}"}`).

*   **Backend Logic**:
    *   The `com.example.communityhttpclient.environments.EnvironmentManager` class is responsible for finding, parsing, and merging `http-client.env.json` and `http-client.private.env.json` files. It loads all defined environments and their variables.
    *   A utility class, `com.example.communityhttpclient.utils.VariableSubstitutor` (which you'll need to create), will be responsible for taking a string (like a URL or header value) and replacing all `{{variable_name}}` placeholders with their actual values from the currently active environment.

*   **Environment Selection**:
    *   To switch between different sets of variables (e.g., development, staging), users will need a UI element, typically a dropdown menu in the editor toolbar or a status bar widget.
    *   This UI component is not yet implemented in the current plugin skeleton. However, the `EnvironmentManager` class provides the necessary backend support with methods like `setActiveEnvironmentName(String name)` and `getActiveEnvironmentName()` to manage which environment's variables should be used for substitution.

*   **Examples**:
    *   Refer to the `http-client.env.json` file in the project root for an example of how to define environments and variables.
    *   The `requests-env-examples.http` file demonstrates how these variables can be used within HTTP requests. For instance, `{{host}}` and `{{auth_token}}` will be replaced by values from the active environment when the request is processed.

### Displaying Formatted HTTP Responses
*   **Purpose**: Raw HTTP response bodies, especially for structured data like JSON or XML, can be difficult to read. Pretty-printing this content significantly improves readability and makes it easier for users to inspect and understand API responses.

*   **Content-Type Detection**:
    *   The first step in formatting a response is to determine its data type. This is done by inspecting the `Content-Type` header returned by the server (e.g., `application/json; charset=utf-8`, `text/xml`, `image/png`).
    *   The plugin should parse this header to extract the main media type (e.g., `application/json`, `text/xml`) before deciding on a formatting strategy.

*   **`ResponseFormatterService`**:
    *   The `com.example.communityhttpclient.services.ResponseFormatterService` is designed for this purpose.
    *   It takes the raw response body (as a string) and the full `Content-Type` string as input.
    *   Based on the content type, it attempts to format the body:
        *   **JSON**: Uses the GSON library for pretty-printing.
        *   **XML**: Uses `javax.xml.transform` (standard Java XML APIs) for pretty-printing.
        *   Other types (e.g., `text/html`, `text/plain`) might be returned raw or with minimal formatting.
    *   If the content type is not recognized or formatting fails, it generally returns the raw body or a placeholder message.

*   **Displaying with Syntax Highlighting**:
    *   To display the (potentially formatted) response body to the user, the IntelliJ Platform provides `com.intellij.ui.EditorTextField`. This component is essentially a lightweight editor instance.
    *   Crucially, you can associate a `FileType` with an `EditorTextField`. By setting the appropriate `FileType` based on the response's `Content-Type`, `EditorTextField` will automatically apply syntax highlighting:
        *   `JsonFileType.INSTANCE` for JSON content.
        *   `XmlFileType.INSTANCE` for XML content.
        *   `HtmlFileType.INSTANCE` for HTML content.
        *   `PlainTextFileType.INSTANCE` for plain text or if no other specific type matches.
    *   The developer will need to implement the UI that hosts this `EditorTextField`, such as a dedicated "Response" tab in a tool window, which might also show headers, status codes, etc.

*   **Handling Binary Content**:
    *   For binary content types (e.g., `image/jpeg`, `application/pdf`), the `ResponseFormatterService` typically returns a placeholder message (e.g., "[Binary content (image/jpeg) - Not displayable as text]").
    *   The UI displaying the response should detect such placeholders and ideally offer an option for the user to "Save to file" to download the binary content. Direct display of arbitrary binary data in a text field is usually not feasible or useful.

### Code Completion (Intelligent Suggestions)
*   **Purpose**: Code completion (also known as "intellisense" or "autocomplete") significantly enhances user productivity and reduces errors when writing `.http` files. By providing context-aware suggestions for HTTP methods, headers, and other elements, it speeds up the editing process and helps users discover available options without constant reference to documentation.

*   **`HTTPCompletionContributor`**:
    *   The core class responsible for providing these suggestions is `com.example.communityhttpclient.completion.HTTPCompletionContributor`.
    *   This class extends `com.intellij.codeInsight.completion.CompletionContributor` from the IntelliJ Platform API, which is the standard way to hook into the completion mechanism.

*   **Implemented Completions**:
    *   The current skeleton includes basic completion for:
        *   **HTTP Methods**: Suggests common methods like `GET`, `POST`, `PUT`, `DELETE`, etc., when the cursor is at a position where a method is expected (typically at the beginning of a request definition).
        *   **Common HTTP Header Names**: Provides a list of standard header names like `Content-Type`, `Authorization`, `Accept`, etc., when the user starts typing in the header section of a request.
        *   **HTTP Protocol Versions**: Offers `HTTP/1.1` and `HTTP/2` when the user is typing the protocol version on the request line (e.g., after the URL).

*   **Mechanism (Briefly)**:
    *   Completions are registered within the `HTTPCompletionContributor`'s constructor using the `extend(CompletionType, ElementPattern, CompletionProvider)` method.
        *   `CompletionType.BASIC` is typically used.
        *   `com.intellij.patterns.PlatformPatterns` (and custom `PatternCondition`s) are used to define the context (i.e., where in the code the completion should trigger). For example, a pattern might specify "the cursor is at the start of a line within an HTTPFile, and the line does not contain a comment".
        *   `com.intellij.codeInsight.completion.CompletionProvider` is an interface whose implementations are responsible for generating the list of suggested items (`LookupElementBuilder`) for a given context.
    *   Currently, due to the basic nature of the lexer and parser, the patterns and conditions in `HTTPCompletionContributor` rely partly on textual heuristics (analyzing the text around the cursor). As the parser becomes more sophisticated and can build a detailed PSI (Program Structure Interface) tree, these patterns can be made more precise by targeting specific PSI elements (e.g., "cursor is inside an `HttpHeaderNameElement`").

*   **Registration**:
    *   To activate the completion contributor, it must be registered in the `plugin.xml` file using the `completion.contributor` extension point. The registration specifies the language ("HTTP") for which the contributor is active and its implementation class:
    ```xml
    <extensions defaultExtensionNs="com.intellij">
        <!-- ... other extensions ... -->
        <completion.contributor
            language="HTTP"
            implementationClass="com.example.communityhttpclient.completion.HTTPCompletionContributor"/>
    </extensions>
    ```

*   **Future Enhancements**:
    *   The completion framework is highly extensible. Future improvements could include:
        *   **Header Values**: Suggestions for common values of specific headers (e.g., `application/json` for `Content-Type`).
        *   **Dynamic Variables**: Completion for variables defined in environment files (e.g., `{{base_url}}`).
        *   **Path Completion**: Suggestions for URL paths based on local project files or even linked OpenAPI/Swagger specifications.
        *   **Live Templates**: Predefined snippets for common request structures.
