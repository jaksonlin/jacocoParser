package com.github.jaksonlin.jacocoparser.util;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ModifiedLineCount {
    public Map<String, Integer> extractFunctionLines(String filePath, List<Integer> modifiedLines) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        CompilationUnit cu = StaticJavaParser.parse(content);
        Set<Integer> modifiedLinesSet = new HashSet<>(modifiedLines);

        // Get all comment lines
        Set<Integer> commentLines = new HashSet<>();
        for (Comment comment : cu.getAllContainedComments()) {
            comment.getRange().ifPresent(range -> {
                IntStream.rangeClosed(range.begin.line, range.end.line)
                    .forEach(commentLines::add);
            });
        }

        // Get all class property lines
        Set<Integer> propertyLines = new HashSet<>();
        for (FieldDeclaration field : cu.findAll(FieldDeclaration.class)) {
            field.getRange().ifPresent(range -> {
                IntStream.rangeClosed(range.begin.line, range.end.line)
                    .forEach(propertyLines::add);
            });
        }

        // Remove comment and property lines from modifiedLinesSet
        modifiedLinesSet.removeAll(commentLines);
        modifiedLinesSet.removeAll(propertyLines);

        Map<String, Integer> result = new HashMap<>();
        for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
            if (method.getRange().isPresent()) {
                int startLine = method.getRange().get().begin.line;
                int endLine = method.getRange().get().end.line;
                boolean hasModifiedLines = modifiedLinesSet.stream()
                    .anyMatch(line -> line >= startLine && line <= endLine);
                if (hasModifiedLines) {
                    int lineCount = countNonCommentLines(method, commentLines);
                    result.put(method.getNameAsString(), lineCount);
                }
            }
        }

        return result;
    }

    private int countNonCommentLines(MethodDeclaration method, Set<Integer> commentLines) {
        if (!method.getRange().isPresent()) {
            return 0;
        }
        int startLine = method.getRange().get().begin.line;
        int endLine = method.getRange().get().end.line;
        int totalLines = endLine - startLine + 1;
        int commentLinesInMethod = (int) IntStream.rangeClosed(startLine, endLine)
            .filter(commentLines::contains)
            .count();
        return totalLines - commentLinesInMethod;
    }
}