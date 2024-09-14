package com.github.jaksonlin.jacocoparser.model;

import java.util.List;

public class GitLabDiff {
    private String oldPath;
    private String newPath;
    private List<String> diff;

    // Getters and setters
    public String getOldPath() {
        return oldPath;
    }

    public void setOldPath(String oldPath) {
        this.oldPath = oldPath;
    }

    public String getNewPath() {
        return newPath;
    }

    public void setNewPath(String newPath) {
        this.newPath = newPath;
    }

    public List<String> getDiff() {
        return diff;
    }

    public void setDiff(List<String> diff) {
        this.diff = diff;
    }
}
