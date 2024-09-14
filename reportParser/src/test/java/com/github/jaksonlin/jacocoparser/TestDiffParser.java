package com.github.jaksonlin.jacocoparser;

import org.junit.Assert;
import org.junit.Test;

import com.github.jaksonlin.jacocoparser.util.DiffParser;

public class TestDiffParser {
    @Test
    public void testDiffParser(){
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
