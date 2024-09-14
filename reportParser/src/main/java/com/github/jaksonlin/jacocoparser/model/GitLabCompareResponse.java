package com.github.jaksonlin.jacocoparser.model;

import java.util.List;

public class GitLabCompareResponse {
    private List<GitLabDiff> diffs;

    // Getters and setters
    public List<GitLabDiff> getDiffs() {
        return diffs;
    }

    public void setDiffs(List<GitLabDiff> diffs) {
        this.diffs = diffs;
    }
}
