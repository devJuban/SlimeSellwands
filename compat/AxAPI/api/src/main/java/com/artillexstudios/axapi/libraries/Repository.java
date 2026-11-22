package com.artillexstudios.axapi.libraries;

import java.net.URI;

public class Repository {
    private final String url;

    public Repository(String url) {
        this.url = url;
    }

    public String url() {
        return this.url;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Repository)) {
            return false;
        }
        Repository that = (Repository) object;
        return java.util.Objects.equals(this.url, that.url);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(url);
    }


    public URI getJarURI(Library library) {
        return URI.create(this.getPath(library) + ".jar");
    }

    public URI getPomURI(Library library) {
        return URI.create(this.getPath(library) + ".pom");
    }

    public URI getMd5ChecksumURI(Library library) {
        return URI.create(this.getPath(library) + ".jar.md5");
    }

    private String getPath(Library library) {
        return this.url + String.format("%s/%s/%s/%s-%s%s", library.group().replace(".", "/"), library.artifactId(), library.version(), library.artifactId(), library.version(), library.classifier() == null || library.classifier().trim().isEmpty() ? "" : '-' + library.classifier());
    }
    @Override
    public String toString() {
        return "Repository[url=" + this.url + "]";
    }

}
