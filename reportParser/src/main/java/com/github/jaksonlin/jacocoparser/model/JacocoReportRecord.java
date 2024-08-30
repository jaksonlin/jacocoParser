package com.github.jaksonlin.jacocoparser.model;


import java.util.Optional;

/**
 *  jacoco report table one line per record
 */
public class JacocoReportRecord {

    String url="";
    String element = "";
    String href= "";
    String instructCoverage= "";
    String branchCoverage= "";
    String missedBranch= "";
    String complexity= "";
    String missedMethod= "";
    String numberOfMethod= "";
    String missedLine= "";
    String numberOfLine= "";
    String missedClass= "";
    String numberOfClass= "";
    JacocoReport subReport;
    long missedBranchCount;
    long complexityCount;
    long missedMethodCount;
    long numberOfMethodCount;
    long missedLineCount;
    long numberOfLineCount;
    long missedClassCount;

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getInstructCoverage() {
        return instructCoverage;
    }

    public void setInstructCoverage(String instructCoverage) {
        this.instructCoverage = instructCoverage;
    }

    public String getBranchCoverage() {
        return branchCoverage;
    }

    public void setBranchCoverage(String branchCoverage) {
        this.branchCoverage = branchCoverage;
    }

    public String getMissedBranch() {
        return missedBranch;
    }

    public void setMissedBranch(String missedBranch) {
        this.missedBranch = missedBranch;
    }

    public String getComplexity() {
        return complexity;
    }

    public void setComplexity(String complexity) {
        this.complexity = complexity;
    }

    public String getMissedMethod() {
        return missedMethod;
    }

    public void setMissedMethod(String missedMethod) {
        this.missedMethod = missedMethod;
    }

    public String getNumberOfMethod() {
        return numberOfMethod;
    }

    public void setNumberOfMethod(String numberOfMethod) {
        this.numberOfMethod = numberOfMethod;
    }

    public String getMissedLine() {
        return missedLine;
    }

    public void setMissedLine(String missedLine) {
        this.missedLine = missedLine;
    }

    public String getNumberOfLine() {
        return numberOfLine;
    }

    public void setNumberOfLine(String numberOfLine) {
        this.numberOfLine = numberOfLine;
    }

    public String getMissedClass() {
        return missedClass;
    }

    public void setMissedClass(String missedClass) {
        this.missedClass = missedClass;
    }

    public String getNumberOfClass() {
        return numberOfClass;
    }

    public void setNumberOfClass(String numberOfClass) {
        this.numberOfClass = numberOfClass;
    }

    public Optional<JacocoReport> getSubReport() {
        return Optional.ofNullable(subReport);
    }

    public void setSubReport(JacocoReport subReport) {
        this.subReport = subReport;
    }

    public long getMissedBranchCount() {
        return missedBranchCount;
    }

    public void setMissedBranchCount(long missedBranchCount) {
        this.missedBranchCount = missedBranchCount;
    }

    public long getComplexityCount() {
        return complexityCount;
    }

    public void setComplexityCount(long complexityCount) {
        this.complexityCount = complexityCount;
    }

    public long getMissedMethodCount() {
        return missedMethodCount;
    }

    public void setMissedMethodCount(long missedMethodCount) {
        this.missedMethodCount = missedMethodCount;
    }

    public long getNumberOfMethodCount() {
        return numberOfMethodCount;
    }

    public void setNumberOfMethodCount(long numberOfMethodCount) {
        this.numberOfMethodCount = numberOfMethodCount;
    }

    public long getMissedLineCount() {
        return missedLineCount;
    }

    public void setMissedLineCount(long missedLineCount) {
        this.missedLineCount = missedLineCount;
    }

    public long getNumberOfLineCount() {
        return numberOfLineCount;
    }

    public void setNumberOfLineCount(long numberOfLineCount) {
        this.numberOfLineCount = numberOfLineCount;
    }

    public long getMissedClassCount() {
        return missedClassCount;
    }

    public void setMissedClassCount(long missedClassCount) {
        this.missedClassCount = missedClassCount;
    }

    public long getNumberOfClassCount() {
        return numberOfClassCount;
    }

    public void setNumberOfClassCount(long numberOfClassCount) {
        this.numberOfClassCount = numberOfClassCount;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    long numberOfClassCount;

    public void convertNumericFields() {
        // parse the numeric fields if they are not empty
        if (!missedBranch.isEmpty()) {
            missedBranchCount = Long.parseLong(missedBranch);
        }
        if (!complexity.isEmpty()) {
            complexityCount = Long.parseLong(complexity);
        }
        if (!missedMethod.isEmpty()) {
            missedMethodCount = Long.parseLong(missedMethod);
        }
        if (!numberOfMethod.isEmpty()) {
            numberOfMethodCount = Long.parseLong(numberOfMethod);
        }
        if (!missedLine.isEmpty()) {
            missedLineCount = Long.parseLong(missedLine);
        }
        if (!numberOfLine.isEmpty()) {
            numberOfLineCount = Long.parseLong(numberOfLine);
        }
        if (!missedClass.isEmpty()) {
            missedClassCount = Long.parseLong(missedClass);
        }
        if (!numberOfClass.isEmpty()) {
            numberOfClassCount = Long.parseLong(numberOfClass);
        }

    }

    public Optional<String> getRecordClassName() {
        return Optional.ofNullable(recordClassName);
    }

    private String recordClassName;

    public Optional<String> getRecordPackageName() {
        return Optional.ofNullable(recordPackageName);
    }

    private String recordPackageName;

    // generate the fully qualified class name from the url
    public Optional<String> getRecordFullyQualifiedClassName() {

        if (url.isEmpty()) {
            return Optional.empty();
        }

        String[] parts = url.split("/");
        if (parts.length < 2) {
            return Optional.empty();
        }
        // take the last 2 part of the url
        String className = parts[parts.length - 1];
        // remove the Lxx suffix
        className = className.replaceAll("\\.html#L\\d+", "");
        this.recordClassName = className;

        String packageName = parts[parts.length - 2];
        this.recordPackageName = packageName;

        String recordFullyQualifiedClassName = packageName + "." + className;
        return Optional.of(recordFullyQualifiedClassName);

    }
}
