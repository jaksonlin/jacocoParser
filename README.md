# README

## Description

A simple jacoco html report parser.

## Usage

Get all the class's covered lines and missed lines from the jacoco html report.
these are store in the ClassCodeCoverage object 's missedLines and coveredLines.'

``` Java
public void TestFacade() {

    try {
            String url = "http://127.0.0.1:28080/html/index.html";

            IJacocoReportParser parser = JacocoParserFactory.NewHtmlReportParser();
            List<ClassCodeCoverage> cov = parser.GetClassCodeCoverages(url);
            if (cov.isEmpty()) {
                System.out.println("TestFacade failed");
                Assert.fail();
            }
            for (ClassCodeCoverage c : cov) {
                if (c.getClassName().isEmpty()) {
                    Assert.fail();
                }
                if (c.getPackageName().isEmpty()) {
                    Assert.fail();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
 }
```
