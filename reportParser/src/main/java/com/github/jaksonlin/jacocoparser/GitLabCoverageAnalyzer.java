package com.github.jaksonlin.jacocoparser;


import com.github.jaksonlin.jacocoparser.model.GitLabDiff;
import com.github.jaksonlin.jacocoparser.util.GitLabApiClient;
import com.github.jaksonlin.jacocoparser.util.ModifiedLineCount;
import com.github.jaksonlin.jacocoparser.util.DiffParser;

import java.util.*;
import java.util.stream.Collectors;

public class GitLabCoverageAnalyzer {
    private final GitLabApiClient gitLabApiClient;
    private final ModifiedLineCount modifiedLineCount;

    public GitLabCoverageAnalyzer(String gitLabUrl, String privateToken) {
        this.gitLabApiClient = new GitLabApiClient(gitLabUrl, privateToken);
        this.modifiedLineCount = new ModifiedLineCount();
    }

    public Map<String, Map<String, Integer>> analyzeModifiedCode(String projectId, String fromCommit, String toCommit) throws Exception {
        List<GitLabDiff> diffs = gitLabApiClient.getDiffs(projectId, fromCommit, toCommit);
        
        Map<String, Map<String, Integer>> result = new HashMap<>();
        
        for (GitLabDiff diff : diffs) {
            if (isJavaSourceFile(diff.getNewPath())) {
                DiffParser.DiffInfo diffInfo = DiffParser.parseDiff(String.join("\n", diff.getDiff()));
                Map<String, Integer> methodLines = modifiedLineCount.extractFunctionLines(diff.getNewPath(), new ArrayList<>(diffInfo.addedLines));
                if (!methodLines.isEmpty()) {
                    result.put(diff.getNewPath(), methodLines);
                }
            }
        }
        
        return result;
    }

    private boolean isJavaSourceFile(String filePath) {
        return filePath.endsWith(".java") && !filePath.toLowerCase().contains("test");
    }

    public int getTotalModifiedLines(Map<String, Map<String, Integer>> analysisResult) {
        return analysisResult.values().stream()
                .flatMap(map -> map.values().stream())
                .mapToInt(Integer::intValue)
                .sum();
    }

    public Map<String, Integer> getModifiedMethodsCount(Map<String, Map<String, Integer>> analysisResult) {
        return analysisResult.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().size()
                ));
    }
}