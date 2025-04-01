package com.github.jaksonlin.jacocoparser.util;


import org.junit.Assert;
import org.junit.Test;

import com.github.jaksonlin.jacocoparser.util.DiffParser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TestDiffParser {




    @Test
    public void HelloWorld(){
        //step 1
        String diff = "@@ -24,8 +24,10 @@\n //= require g.raphael-min\n //= require g.bar-min\n //= require branch-graph\n-//= require highlightjs.min\n-//= require ace/ace\n //= require_tree .\n //= require d3\n //= require underscore\n+\n+function fix() { \n+  alert(\"Fixed\")\n+}";
        DiffParser.DiffInfo diffInfo = DiffParser.parseDiff(diff);
        System.out.println(diffInfo.addedLines);
        System.out.println(diffInfo.removedLines);
        System.out.println(diffInfo.modifiedLines);
        System.out.println(diffInfo.unchangedLines);
        // assert 2  

        checkDiffInfos(diffInfo.addedLines, 4);
        checkDiffInfos(diffInfo.removedLines, 2);
        checkDiffInfos(diffInfo.modifiedLines, 0);
        checkDiffInfos(diffInfo.unchangedLines, 6);

    }

    private void checkDiffInfos(List<Integer> listToCheck, int expectedSize){
        Assert.assertEquals(listToCheck.size(),1);
    }



    @Test
    public void testParseDiff(){
        //step 1
        String diff = "@@ -24,8 +24,10 @@\n //= require g.raphael-min\n //= require g.bar-min\n //= require branch-graph\n-//= require highlightjs.min\n-//= require ace/ace\n //= require_tree .\n //= require d3\n //= require underscore\n+\n+function fix() { \n+  alert(\"Fixed\")\n+}";
        DiffParser.DiffInfo diffInfo = DiffParser.parseDiff(diff);
        System.out.println(diffInfo.addedLines);
        System.out.println(diffInfo.removedLines);
        System.out.println(diffInfo.modifiedLines);
        System.out.println(diffInfo.unchangedLines);
        // assert 2
        Assert.assertEquals(diffInfo.addedLines.size(), 4);
        Assert.assertEquals(diffInfo.removedLines.size(), 2);
        Assert.assertEquals(diffInfo.modifiedLines.size(), 0);
        Assert.assertEquals(diffInfo.unchangedLines.size(), 6);

    }



    @Test(expected = NullPointerException.class)
    public void testParseDiff_test2(){
        // step 1
        String diff = "@@ -24,8 +24,10 @@\n //= require g.raphael-min\n //= require g.bar-min\n //= require branch-graph\n-//= require highlightjs.min\n-//= require ace/ace\n //= require_tree .\n //= require d3\n //= require underscore\n+\n+function fix() { \n+  alert(\"Fixed\")\n+}";
        DiffParser.DiffInfo diffInfo = DiffParser.parseDiff(diff);
        System.out.println(diffInfo.addedLines);
        System.out.println(diffInfo.removedLines);
        System.out.println(diffInfo.modifiedLines);
        System.out.println(diffInfo.unchangedLines);
        // assert 1

    }






    @Test
    public void testHelloMessage_test1(){
        // step 1
        String diff = "@@ -24,8 +24,10 @@\n //= require g.raphael-min\n //= require g.bar-min\n //= require branch-graph\n-//= require highlightjs.min\n-//= require ace/ace\n //= require_tree .\n //= require d3\n //= require underscore\n+\n+function fix() { \n+  alert(\"Fixed\")\n+}";
        DiffParser.DiffInfo diffInfo = DiffParser.parseDiff(diff);

        
        System.out.println(diffInfo.addedLines);
        System.out.println(diffInfo.removedLines);
        System.out.println(diffInfo.modifiedLines);
        System.out.println(diffInfo.unchangedLines);
        // assert 1
        myTestCheckHelper(diffInfo.addedLines, 4);


    }

    private void myTestCheckHelper(List<Integer> check, int size){
        Assert.assertEquals(check.size(), size);
    }

    @Test
    public void testParseDiffWithIncorrectLineCount() {
        // step 1
        String diffContent = "--- a/file.java\n+++ b/file.java\n@@ -1,2 +1,3 @@\n+new line";
        DiffParser parser = new DiffParser();
        DiffParser.DiffInfo diffInfo = parser.parseDiff(diffContent);
        // assert 1
        // Assertion to ensure correct line counts
        Assert.assertEquals(0, diffInfo.removedLines.size());
        Assert.assertEquals(1, diffInfo.addedLines.size());
    }


}
