package hr.documentcloud.model;

import java.io.Serializable;
import java.util.*;

public class DirectoryStructure implements Serializable {

    private static final String DEFAULT_DIRECTORY_DELIMITER = "/";

    private final String directoryName;
    private final Set<DirectoryStructure> children;
    private final String directoryDelimiter;

    private DirectoryStructure(String directoryName, String directoryDelimiter) {
        this.directoryName = directoryName;
        this.directoryDelimiter = directoryDelimiter;
        children = new HashSet<>();
    }

    public static DirectoryStructure fromPath(String path) {
        return fromPath(path, DEFAULT_DIRECTORY_DELIMITER);
    }

    public static DirectoryStructure fromPath(String path, String directoryDelimiter) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be empty.");
        }

        List<String> parts = Arrays.asList(path.split(directoryDelimiter));

        DirectoryStructure directoryStructure = new DirectoryStructure(parts.get(0), directoryDelimiter);
        directoryStructure.updateStructure(path);

        return directoryStructure;
    }

    public void updateStructure(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be empty.");
        }

        List<String> parts = new LinkedList<>(Arrays.asList(path.split(directoryDelimiter)));

        if (parts.get(0).equals(directoryName)) {
            parts.remove(0);
        }

        if (parts.isEmpty()) {
            return;
        }

        String childName = parts.get(0);

        Optional<DirectoryStructure> maybeExistingChild = children.stream()
                .filter(c -> c.getDirectoryName().equals(childName))
                .findFirst();

        DirectoryStructure child;
        if (maybeExistingChild.isPresent()) {
            child = maybeExistingChild.get();
        } else {
            child = new DirectoryStructure(childName, directoryDelimiter);
            children.add(child);
        }

        String pathStartingWithChild = String.join(directoryDelimiter, parts);
        child.updateStructure(pathStartingWithChild);
    }

    public String getDirectoryName() {
        return directoryName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendStructure(sb, 0);
        return sb.toString();
    }

    private void appendStructure(StringBuilder sb, int indentation) {
        for (int i = 0; i < indentation; i++) {
            sb.append(" ");
        }

        sb.append(directoryName);
        sb.append("\n");

        for (DirectoryStructure child : children) {
            child.appendStructure(sb, indentation + 4);
        }
    }

}