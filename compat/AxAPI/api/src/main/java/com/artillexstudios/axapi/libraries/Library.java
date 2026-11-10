package com.artillexstudios.axapi.libraries;

import java.util.List;
import java.util.Objects;

public class Library {
    private final String group;
    private final String artifactId;
    private final String version;
    private final String classifier;
    private final List<Library> transitiveDependencies;

    public Library(String group, String artifactId, String version, String classifier, List<Library> transitiveDependencies) {
        this.group = group;
        this.artifactId = artifactId;
        this.version = version;
        this.classifier = classifier;
        this.transitiveDependencies = transitiveDependencies;
    }

    public String group() {
        return this.group;
    }

    public String artifactId() {
        return this.artifactId;
    }

    public String version() {
        return this.version;
    }

    public String classifier() {
        return this.classifier;
    }

    public List<Library> transitiveDependencies() {
        return this.transitiveDependencies;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Library)) {
            return false;
        }
        Library library = (Library) o;

        return Objects.equals(this.group, library.group) && Objects.equals(this.version, library.version) && Objects.equals(this.artifactId, library.artifactId) && Objects.equals(this.classifier, library.classifier);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(this.group);
        result = 31 * result + Objects.hashCode(this.artifactId);
        result = 31 * result + Objects.hashCode(this.version);
        result = 31 * result + Objects.hashCode(this.classifier);
        return result;
    }

        @Override
    public String toString() {
        return this.group + ":" + this.artifactId + ":" + this.version;
    }
}
