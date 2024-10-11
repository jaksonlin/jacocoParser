package com.github.jaksonlin.jacocoparser;

import com.github.jaksonlin.jacocoparser.model.GitLabDiff;
import com.github.jaksonlin.jacocoparser.util.GitLabApiClient;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GitLabCoverageAnalyzerTest {

    @Mock
    private GitLabApiClient mockGitLabApiClient;

    private GitLabCoverageAnalyzer analyzer;
    
    private MockedStatic<Files> mockedFiles;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        analyzer = new GitLabCoverageAnalyzer("http://gitlab.example.com", "dummy_token");
   
        injectMockGitLabApiClient(analyzer, mockGitLabApiClient);
        mockedFiles = mockStatic(Files.class);
    }
    private void injectMockGitLabApiClient(GitLabCoverageAnalyzer analyzer, GitLabApiClient mockClient) throws Exception {
        Field field = GitLabCoverageAnalyzer.class.getDeclaredField("gitLabApiClient");
        field.setAccessible(true);
        field.set(analyzer, mockClient);
    }

    @After
    public void tearDown() {
        mockedFiles.close();
    }

    @Test
    public void testAnalyzeModifiedCode() throws Exception {
        // Prepare test data
        GitLabDiff diff1 = new GitLabDiff();
        diff1.setNewPath("src/main/java/com/example/Class1.java");
        diff1.setDiff(Arrays.asList(
            "@@ -1,5 +1,7 @@",
            " package com.example;",
            " ",
            " public class Class1 {",
            "+    public void newMethod() {",
            "+        System.out.println(\"New method\");",
            "+    }",
            " ",
            "     public void existingMethod() {"
        ));

        GitLabDiff diff2 = new GitLabDiff();
        diff2.setNewPath("src/test/java/com/example/TestClass.java");
        diff2.setDiff(Arrays.asList(
            "@@ -1,3 +1,5 @@",
            " package com.example;",
            " ",
            "-// This is a test class"
        ));

        String class1Content = "package com.example;\n" +
        " \n" +
        "public class Class1 {\n" +
        "    public void newMethod() {\n" +
        "        System.out.println(\"New method\");\n" +
        "    }\n" +
        "}";
        String testClassContent = "package com.example;\n" +
        " \n" +
        "public class TestClass {\n" +
        "    public void newMethod() {\n" +
        "        System.out.println(\"New method\");\n" +
        "    }\n" +
        "}";

        // Mock GitLabApiClient behavior
        when(mockGitLabApiClient.getDiffs("project_id", "from_commit", "to_commit"))
            .thenReturn(Arrays.asList(diff1, diff2));

        // Mock static file operations
        Path mockPath = Paths.get("src/main/java/com/example/Class1.java");
        mockedFiles.when(() -> Files.exists(mockPath)).thenReturn(true);
        mockedFiles.when(() -> Files.isRegularFile(mockPath)).thenReturn(true);
        // mock the files.readallbytes from Class1.java
        mockedFiles.when(() -> Files.readAllBytes(mockPath)).thenAnswer(invocation -> {
            // if the input path is src/main/java/com/example/Class1.java, return the content of Class1.java
            if (invocation.getArgument(0).toString().contains("Class1.java")) {
                return class1Content.getBytes();
            } else {
                return testClassContent.getBytes();
            }
        });

        // Run the analysis
        Map<String, Map<String, Integer>> result = analyzer.analyzeModifiedCode("project_id", "from_commit", "to_commit");

        // Verify the results
        assertEquals(1, result.size());
        assertTrue(result.containsKey("src/main/java/com/example/Class1.java"));
        
        Map<String, Integer> methodLines = result.get("src/main/java/com/example/Class1.java");
        assertEquals(1, methodLines.size());
        assertTrue(methodLines.containsKey("newMethod"));
        assertEquals(Integer.valueOf(3), methodLines.get("newMethod"));

        // Verify that the test file was ignored
        assertFalse(result.containsKey("src/test/java/com/example/TestClass.java"));
    }

    @Test
    public void testGetTotalModifiedLines() {
        Map<String, Map<String, Integer>> analysisResult = new HashMap<>();
        Map<String, Integer> file1Methods = new HashMap<>();
        file1Methods.put("method1", 5);
        file1Methods.put("method2", 3);
        analysisResult.put("File1.java", file1Methods);
        Map<String, Integer> file2Methods = new HashMap<>();
        file2Methods.put("method3", 2);
        analysisResult.put("File2.java", file2Methods);

        int totalLines = analyzer.getTotalModifiedLines(analysisResult);
        assertEquals(10, totalLines);
    }

    @Test
    public void testGetModifiedMethodsCount() {
        Map<String, Map<String, Integer>> analysisResult = new HashMap<>();
        Map<String, Integer> file1Methods = new HashMap<>();
        file1Methods.put("method1", 5);
        file1Methods.put("method2", 3);
        analysisResult.put("File1.java", file1Methods);
        Map<String, Integer> file2Methods = new HashMap<>();
        file2Methods.put("method3", 2);
        analysisResult.put("File2.java", file2Methods);
        
        

        Map<String, Integer> methodCounts = analyzer.getModifiedMethodsCount(analysisResult);
        assertEquals(2, methodCounts.size());
        assertEquals(Integer.valueOf(2), methodCounts.get("File1.java"));
        assertEquals(Integer.valueOf(1), methodCounts.get("File2.java"));
    }
}