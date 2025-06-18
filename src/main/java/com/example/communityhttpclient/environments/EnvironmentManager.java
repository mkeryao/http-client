package com.example.communityhttpclient.environments;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
// For JSON parsing, you might use IntelliJ's built-in parser, GSON, or Jackson.
// This example will use a conceptual manual parsing for simplicity of the stub.
// import com.google.gson.Gson;
// import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import com.intellij.json.JsonParser; // Requires dependency on com.intellij.json
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.json.psi.*;


public class EnvironmentManager {

    private static final String ENV_FILE_NAME = "http-client.env.json";
    private static final String PRIVATE_ENV_FILE_NAME = "http-client.private.env.json";

    private final Project project;
    private final Map<String, Environment> environments = new ConcurrentHashMap<>();
    private String activeEnvironmentName; // Needs UI to set this

    public EnvironmentManager(Project project) {
        this.project = project;
        loadAllEnvironments();
    }

    public void loadAllEnvironments() {
        environments.clear();
        // Load public environments
        Map<String, Map<String, String>> publicEnvMaps = parseEnvFile(project, ENV_FILE_NAME);
        // Load private environments (will override public ones)
        Map<String, Map<String, String>> privateEnvMaps = parseEnvFile(project, PRIVATE_ENV_FILE_NAME);

        // Merge public and private environments
        // Start with public, then update/add with private
        Map<String, Map<String, String>> mergedEnvMaps = new HashMap<>(publicEnvMaps);
        privateEnvMaps.forEach((envName, privateVars) ->
            mergedEnvMaps.merge(envName, privateVars, (publicVars, privVars) -> {
                Map<String, String> mergedVars = new HashMap<>(publicVars);
                mergedVars.putAll(privVars);
                return mergedVars;
            })
        );

        mergedEnvMaps.forEach((name, vars) -> environments.put(name, new Environment(name, vars)));

        // Set a default active environment if none is set and there are environments
        if (activeEnvironmentName == null && !environments.isEmpty()) {
            // Prefer 'development' or the first one found
            if (environments.containsKey("development")) {
                activeEnvironmentName = "development";
            } else {
                activeEnvironmentName = environments.keySet().iterator().next();
            }
        }
    }

    private Map<String, Map<String, String>> parseEnvFile(Project project, String fileName) {
        Map<String, Map<String, String>> allEnvVariables = new HashMap<>();
        VirtualFile projectDir = LocalFileSystem.getInstance().findFileByPath(project.getBasePath());
        if (projectDir == null) {
            return Collections.emptyMap();
        }

        VirtualFile envFile = projectDir.findChild(fileName);
        if (envFile == null || !envFile.exists() || envFile.isDirectory()) {
            return Collections.emptyMap();
        }

        try {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(envFile);
            if (!(psiFile instanceof JsonFile)) {
                // Log error or handle non-JSON file
                return Collections.emptyMap();
            }
            JsonFile jsonFile = (JsonFile) psiFile;
            JsonValue topLevelValue = jsonFile.getTopLevelValue();

            if (topLevelValue instanceof JsonObject) {
                JsonObject topLevelObject = (JsonObject) topLevelValue;
                for (JsonProperty environmentProperty : topLevelObject.getPropertyList()) {
                    String envName = environmentProperty.getName();
                    JsonValue envValue = environmentProperty.getValue();
                    if (envValue instanceof JsonObject) {
                        Map<String, String> variables = new HashMap<>();
                        for (JsonProperty varProperty : ((JsonObject) envValue).getPropertyList()) {
                            String varName = varProperty.getName();
                            JsonValue varVal = varProperty.getValue();
                            if (varVal instanceof JsonString) {
                                variables.put(varName, ((JsonString) varVal).getValue());
                            } else if (varVal instanceof JsonNumber) {
                                variables.put(varName, varVal.getText());
                            } else if (varVal instanceof JsonBoolean) {
                                variables.put(varName, varVal.getText());
                            }
                            // Add other types if necessary, or decide on string-only values
                        }
                        allEnvVariables.put(envName, variables);
                    }
                }
            }
        } catch (Exception e) {
            // Log error during parsing (e.g., e.printStackTrace())
            // Consider using IntelliJ's logging framework: com.intellij.openapi.diagnostic.Logger
            System.err.println("Error parsing environment file: " + fileName + "; " + e.getMessage());
            return Collections.emptyMap();
        }
        return allEnvVariables;
    }


    public Optional<Environment> getEnvironment(String name) {
        return Optional.ofNullable(environments.get(name));
    }

    public Map<String, Environment> getAllEnvironments() {
        return Collections.unmodifiableMap(environments);
    }

    public Optional<String> getVariableFromActive(String variableName) {
        if (activeEnvironmentName == null) {
            // Attempt to get from a "no-environment" or global scope if defined
            return getEnvironment("no-environment").map(env -> env.getVariable(variableName));
        }
        return getEnvironment(activeEnvironmentName).map(env -> env.getVariable(variableName));
    }

    public Optional<String> getVariable(String environmentName, String variableName) {
        return getEnvironment(environmentName).map(env -> env.getVariable(variableName));
    }

    public String getActiveEnvironmentName() {
        return activeEnvironmentName;
    }

    public void setActiveEnvironmentName(String activeEnvironmentName) {
        // In a real scenario, this might trigger a UI update or event
        if (environments.containsKey(activeEnvironmentName) || activeEnvironmentName == null) {
            this.activeEnvironmentName = activeEnvironmentName;
        } else {
            // Log warning: environment not found
            System.err.println("Attempted to set active environment to unknown: " + activeEnvironmentName);
        }
    }

    // Consider making this a per-project service for easier access
    public static EnvironmentManagergetInstance(Project project) {
        // This is a simplified getInstance. In a real plugin, you'd register this as a project service.
        // For example, using project.getService(EnvironmentManager.class)
        // This would require EnvironmentManager to be registered as a service in plugin.xml
        // For now, this is a conceptual placeholder. A better way is to manage its lifecycle as a service.
        // A simple way for now might be to cache it in project's user data.
        // This is NOT a robust way to get a singleton service.
        return new EnvironmentManager(project); // Replace with proper service lookup
    }
}
