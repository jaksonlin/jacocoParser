package com.github.jaksonlin.jacocoparser.util;

import org.example.annotations.UnittestCaseInfo;
import org.junit.Assert;
import org.junit.Test;

import com.github.jaksonlin.jacocoparser.util.DiffParser;
public class TestDiffParser {





    @UnittestCaseInfo(
            author = "zhislin <zhislin@icloud.com>",
            title = "Test parse diff",
            targetClass = "com.github.jaksonlin.jacocoparser.util.DiffParser",
            targetMethod = "parsediff",
            lastUpdateTime = "2024-12-28 23:59:04",
            lastUpdateAuthor = "zhislin <zhislin@icloud.com>",
            methodSignature = "DiffInfo parseDiff(String diffString)",
            testPoints = {"Functionality"},
            description = "Test parse diff",
            relatedPlan = {"game"}
    )
    @Test
    public void testParseDiff(){
        String diff = "@@ -24,8 +24,10 @@\n //= require g.raphael-min\n //= require g.bar-min\n //= require branch-graph\n-//= require highlightjs.min\n-//= require ace/ace\n //= require_tree .\n //= require d3\n //= require underscore\n+\n+function fix() { \n+  alert(\"Fixed\")\n+}";
        DiffParser.DiffInfo diffInfo = DiffParser.parseDiff(diff);
        System.out.println(diffInfo.addedLines);
        System.out.println(diffInfo.removedLines);
        System.out.println(diffInfo.modifiedLines);
        System.out.println(diffInfo.unchangedLines);
        Assert.assertEquals(diffInfo.addedLines.size(), 4);
        Assert.assertEquals(diffInfo.removedLines.size(), 2);
        Assert.assertEquals(diffInfo.modifiedLines.size(), 0);
        Assert.assertEquals(diffInfo.unchangedLines.size(), 6);


    }





    @UnittestCaseInfo(
            author = "zhislin <zhislin@icloud.com>",
            title = "Test parse diff test2",
            targetClass = "com.github.jaksonlin.jacocoparser.util.DiffParser",
            targetMethod = "parsediff",
            lastUpdateTime = "2024-12-28 23:59:04",
            lastUpdateAuthor = "zhislin <zhislin@icloud.com>",
            methodSignature = "DiffInfo parseDiff(String diffString)",
            testPoints = {"Functionality"},
            description = "Test parse diff test2",
            relatedPlan = {"game"}
    )
    @Test
    public void testParseDiff_test2(){
        String diff = "@@ -24,8 +24,10 @@\n //= require g.raphael-min\n //= require g.bar-min\n //= require branch-graph\n-//= require highlightjs.min\n-//= require ace/ace\n //= require_tree .\n //= require d3\n //= require underscore\n+\n+function fix() { \n+  alert(\"Fixed\")\n+}";
        DiffParser.DiffInfo diffInfo = DiffParser.parseDiff(diff);
        System.out.println(diffInfo.addedLines);
        System.out.println(diffInfo.removedLines);
        System.out.println(diffInfo.modifiedLines);
        System.out.println(diffInfo.unchangedLines);
        Assert.assertEquals(diffInfo.addedLines.size(), 4);
        Assert.assertEquals(diffInfo.removedLines.size(), 2);
        Assert.assertEquals(diffInfo.modifiedLines.size(), 0);
        Assert.assertEquals(diffInfo.unchangedLines.size(), 6);


    }



    @UnittestCaseInfo(
            author = "zhislin <zhislin@icloud.com>",
            title = "Test hello message test1",
            targetClass = "com.github.jaksonlin.jacocoparser.util.DiffParser",
            targetMethod = "hellomessage",
            lastUpdateTime = "2024-12-28 23:59:04",
            lastUpdateAuthor = "zhislin <zhislin@icloud.com>",
            methodSignature = "String HelloMessage()",
            testPoints = {"Functionality"},
            description = "Test hello message test1",
            relatedPlan = {"game"}
    )
    @Test
    public void testHelloMessage_test1(){
        String diff = "@@ -24,8 +24,10 @@\n //= require g.raphael-min\n //= require g.bar-min\n //= require branch-graph\n-//= require highlightjs.min\n-//= require ace/ace\n //= require_tree .\n //= require d3\n //= require underscore\n+\n+function fix() { \n+  alert(\"Fixed\")\n+}";
        DiffParser.DiffInfo diffInfo = DiffParser.parseDiff(diff);
        System.out.println(diffInfo.addedLines);
        System.out.println(diffInfo.removedLines);
        System.out.println(diffInfo.modifiedLines);
        System.out.println(diffInfo.unchangedLines);
        Assert.assertEquals(diffInfo.addedLines.size(), 4);
        Assert.assertEquals(diffInfo.removedLines.size(), 2);
        Assert.assertEquals(diffInfo.modifiedLines.size(), 0);
        Assert.assertEquals(diffInfo.unchangedLines.size(), 6);


    }
}
