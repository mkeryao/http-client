package com.example.communityhttpclient.environments;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class Environment {
    private final String name;
    private final Map<String, String> variables;

    public Environment(String name, Map<String, String> variables) {
        this.name = Objects.requireNonNull(name);
        this.variables = Collections.unmodifiableMap(Objects.requireNonNull(variables));
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    public String getVariable(String variableName) {
        return variables.get(variableName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Environment that = (Environment) o;
        return name.equals(that.name) && variables.equals(that.variables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, variables);
    }

    @Override
    public String toString() {
        return "Environment{name='" + name + "', variables=" + variables.size() + "}";
    }
}
