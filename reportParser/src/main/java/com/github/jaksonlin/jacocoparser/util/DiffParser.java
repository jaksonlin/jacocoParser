
package com.github.jaksonlin.jacocoparser.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiffParser {
    public static DiffInfo parseDiff(String diffString) {
        DiffInfo diffInfo = new DiffInfo();
        String[] lines = diffString.split("\n");
        
        Pattern headerPattern = Pattern.compile("@@ -(\\d+),(\\d+) \\+(\\d+),(\\d+) @@");
        Matcher headerMatcher = headerPattern.matcher(lines[0]);
        
        if (headerMatcher.find()) {
            int oldStart = Integer.parseInt(headerMatcher.group(1));
            int newStart = Integer.parseInt(headerMatcher.group(3));
            
            int oldLineNumber = oldStart;
            int newLineNumber = newStart;
            
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];
                if (line.startsWith("-")) {
                    diffInfo.removedLines.add(oldLineNumber);
                    oldLineNumber++;
                } else if (line.startsWith("+")) {
                    diffInfo.addedLines.add(newLineNumber);
                    newLineNumber++;
                } else {
                    diffInfo.unchangedLines.add(newLineNumber);
                    oldLineNumber++;
                    newLineNumber++;
                }
            }
            
            // Calculate modified lines (lines that exist in both old and new versions)
            diffInfo.modifiedLines.addAll(diffInfo.removedLines);
            diffInfo.modifiedLines.retainAll(diffInfo.addedLines);
        }
        
        return diffInfo;
    }

    public static String HelloMessage() {
        return "Hello";
    }
    
    public static class DiffInfo {
        public List<Integer> addedLines = new ArrayList<>();
        public List<Integer> removedLines = new ArrayList<>();
        public List<Integer> unchangedLines = new ArrayList<>();
        public List<Integer> modifiedLines = new ArrayList<>();
    }
}