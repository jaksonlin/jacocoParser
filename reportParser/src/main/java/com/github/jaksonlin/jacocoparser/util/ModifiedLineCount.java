package com.github.jaksonlin.jacocoparser.util;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/*
 * Excluding methods with only comment modifications
 * Correctly counting lines in methods with actual code changes
 * Handling multi-line comments
 * Treating wrapped long lines as single lines (matching Jacoco's behavior)
 * Excluding class property modifications
 * Counting lines in methods with actual code changes
 */
public class ModifiedLineCount {
    public Map<String, Integer> extractFunctionLines(String filePath, List<Integer> modifiedLines) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        CompilationUnit cu = StaticJavaParser.parse(content);
        Set<Integer> modifiedLinesSet = new HashSet<>(modifiedLines);

        // Get all comment lines
        Set<Integer> commentLines = getCommentLines(cu, content);

        Map<String, Integer> result = new HashMap<>();
        for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
            if (method.getRange().isPresent()) {
                int startLine = method.getRange().get().begin.line;
                int endLine = method.getRange().get().end.line;
                Set<Integer> methodModifiedLines = modifiedLinesSet.stream()
                    .filter(line -> line >= startLine && line <= endLine)
                    .collect(Collectors.toSet());
                
                // Check if there are any modified lines that are not comments
                boolean hasNonCommentModifications = methodModifiedLines.stream()
                    .anyMatch(line -> !commentLines.contains(line));
                
                if (hasNonCommentModifications) {
                    int lineCount = countNonCommentLines(method, content, commentLines);
                    result.put(method.getNameAsString(), lineCount);
                }
            }
        }

        return result;
    }

    private Set<Integer> getCommentLines(CompilationUnit cu, String content) {
        Set<Integer> commentLines = new HashSet<>();
        for (Comment comment : cu.getAllContainedComments()) {
            comment.getRange().ifPresent(range -> {
                int startLine = range.begin.line;
                int endLine = range.end.line;
                for (int i = startLine; i <= endLine; i++) {
                    commentLines.add(i);
                }
            });
        }
        return commentLines;
    }
    private int countNonCommentLines(MethodDeclaration method, String content, Set<Integer> commentLines) {
        if (!method.getRange().isPresent()) {
            return 0;
        }
        int startLine = method.getRange().get().begin.line;
        int endLine = method.getRange().get().end.line;
        
        String[] lines = content.split("\n");
        int count = 0;
        boolean inStringConcatenation = false;
        boolean inAnnotation = false;
        
        for (int i = startLine - 1; i < endLine; i++) {
            if (!commentLines.contains(i + 1)) {
                String trimmedLine = lines[i].trim();
                if (!trimmedLine.isEmpty()) {
                    if (trimmedLine.startsWith("@")) {
                        inAnnotation = true;
                    } else if (inAnnotation && trimmedLine.endsWith(")")) {
                        inAnnotation = false;
                    } else if (!inAnnotation) {
                        if (!inStringConcatenation) {
                            count++;
                        }
                        inStringConcatenation = trimmedLine.endsWith("+");
                    }
                }
            }
        }
        
        return count;
    }
    
    private int countChar(String str, char ch) {
        return (int) str.chars().filter(c -> c == ch).count();
    }
}