package com.github.jaksonlin.jacocoparser.util;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ModifiedLineCountTest {

    private ModifiedLineCount modifiedLineCount;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void setUp() {
        modifiedLineCount = new ModifiedLineCount();
    }

    @Test
    public void testExtractFunctionLines() throws IOException {
        String testCode = 
            "public class TestClass {\n" +
            "    private int field1;\n" +
            "    // Comment line\n" +
            "    public void method1() {\n" +
            "        System.out.println(\"Hello\");\n" +
            "    }\n" +
            "    \n" +
            "    public int method2(int a, int b) {\n" +
            "        // Another comment\n" +
            "        return a + b;\n" +
            "    }\n" +
            "}";

        File file = tempFolder.newFile("TestClass.java");
        Files.write(file.toPath(), testCode.getBytes());

        List<Integer> modifiedLines = Arrays.asList(2, 3, 5, 9, 10);
        Map<String, Integer> result = modifiedLineCount.extractFunctionLines(file.getPath(), modifiedLines);

        assertEquals(2, result.size());
        assertTrue(result.containsKey("method1"));
        assertTrue(result.containsKey("method2"));
        assertEquals(Integer.valueOf(3), result.get("method1"));
        assertEquals(Integer.valueOf(3), result.get("method2")); // not include comment lines
    }

    @Test
    public void testExtractFunctionLinesWithOnlyCommentModification() throws IOException {
        String testCode = 
            "public class TestClass {\n" +
            "    public void method1() {\n" +
            "        // Modified comment\n" +
            "        System.out.println(\"Hello\");\n" +
            "    }\n" +
            "}";

        File file = tempFolder.newFile("TestClass.java");
        Files.write(file.toPath(), testCode.getBytes());

        List<Integer> modifiedLines = Arrays.asList(3);
        Map<String, Integer> result = modifiedLineCount.extractFunctionLines(file.getPath(), modifiedLines);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testExtractFunctionLinesWithOnlyFieldModification() throws IOException {
        String testCode = 
            "public class TestClass {\n" +
            "    private int modifiedField;\n" +
            "    public void method1() {\n" +
            "        System.out.println(\"Hello\");\n" +
            "    }\n" +
            "}";

        File file = tempFolder.newFile("TestClass.java");
        Files.write(file.toPath(), testCode.getBytes());

        List<Integer> modifiedLines = Arrays.asList(2);
        Map<String, Integer> result = modifiedLineCount.extractFunctionLines(file.getPath(), modifiedLines);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testExtractFunctionLinesExcludingComments() throws IOException {
        String testCode = 
            "public class TestClass {\n" +
            "    public void method1() {\n" +
            "        // Comment 1\n" +
            "        System.out.println(\"Hello\");\n" +
            "        /* Multi-line\n" +
            "           comment */\n" +
            "        int x = 1;\n" +
            "        // Comment 2\n" +
            "    }\n" +
            "    \n" +
            "    public int method2() {\n" +
            "        // Comment 3\n" +
            "        return 42;\n" +
            "        // Comment 4\n" +
            "    }\n" +
            "}";

        File file = tempFolder.newFile("TestClass.java");
        Files.write(file.toPath(), testCode.getBytes());

        List<Integer> modifiedLines = Arrays.asList(2, 3, 4, 5, 6, 7, 8, 11, 12, 13, 14);
        Map<String, Integer> result = modifiedLineCount.extractFunctionLines(file.getPath(), modifiedLines);

        assertEquals(2, result.size());
        assertTrue(result.containsKey("method1"));
        assertTrue(result.containsKey("method2"));
        assertEquals(Integer.valueOf(4), result.get("method1")); // 7 lines total, 3 comment lines
        assertEquals(Integer.valueOf(3), result.get("method2")); // 4 lines total, 2 comment lines
    }

    @Test
    public void testLongWrappedLine() throws IOException {
        String testCode = 
            "public class TestClass {\n" +
            "    public void methodWithLongLine() {\n" +
            "        String longLine = \"This is a very long line of code that might be wrapped in an editor \" +\n" +
            "                          \"but is actually just one line as far as Java and Jacoco are concerned\";\n" +
            "    }\n" +
            "}";

        File file = tempFolder.newFile("TestClass.java");
        Files.write(file.toPath(), testCode.getBytes());

        List<Integer> modifiedLines = Arrays.asList(1, 2, 3, 4, 5);
        Map<String, Integer> result = modifiedLineCount.extractFunctionLines(file.getPath(), modifiedLines);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("methodWithLongLine"));
        assertEquals(Integer.valueOf(3), result.get("methodWithLongLine")); // 3 lines: method signature, long line, closing brace
    }

    @Test
    public void testMultiLineConstructs() throws IOException {
        String testCode = 
        "public class TestClass {\n" +
        "    @SuppressWarnings({\n" +
        "        \"deprecation\",\n" +
        "        \"unchecked\"\n" +
        "    })\n" +
        "    public void methodWithMultiLineConstructs() {\n" +
        "        String s = \"Hello \" +\n" +
        "\n" +
        "                   \"World\" +\n" +
        "\n" +
        "                   \"!\";\n" +
        "        System.out.println(s);\n" +
        "        int x = (1 + 2 +\n" +
        "                 3 + 4);\n" +
        "    }\n" +
        "}";

        File file = tempFolder.newFile("TestClass.java");
        Files.write(file.toPath(), testCode.getBytes());

        List<Integer> modifiedLines = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13);
        Map<String, Integer> result = modifiedLineCount.extractFunctionLines(file.getPath(), modifiedLines);
        
        assertEquals(1, result.size());
        assertTrue(result.containsKey("methodWithMultiLineConstructs"));
        assertEquals(Integer.valueOf(5), result.get("methodWithMultiLineConstructs"));
        // 5 lines: method signature, string assignment, println, int assignment, closing brace
    }
}