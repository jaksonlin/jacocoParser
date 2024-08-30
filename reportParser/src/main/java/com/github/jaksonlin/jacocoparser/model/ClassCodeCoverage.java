package com.github.jaksonlin.jacocoparser.model;


import com.github.jaksonlin.jacocoparser.util.Util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;


public class ClassCodeCoverage {

    public ClassCodeCoverage(JacocoReportRecord record) {
        this.record = record;
        this.className = record.getRecordClassName().get();
        this.packageName = record.getRecordPackageName().get();
    }
    List<Integer> coveredLines = new ArrayList<>();
    List<Integer> missedLines = new ArrayList<>();
    String className;
    String packageName;
    JacocoReportRecord record;

    public List<Integer> getCoveredLines() {
        return coveredLines;
    }

    public void addCoveredLine(int line) {
        this.coveredLines.add(line);
    }

    public List<Integer> getMissedLines() {
        return missedLines;
    }

    public void addMissedLines(int line) {
        this.missedLines.add(line);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public byte[] getJacocoRawContent() throws Exception {
        String url = record.getUrl();
        return Util.DownloadWebPage(url);
    }

    protected void loadCoverage() throws Exception {

        byte[] htmlBytes = this.getJacocoRawContent();

        Document doc = Jsoup.parse(new String(htmlBytes));

        for (Element span : doc.select("span.fc, span.pc, span.nc")) {
            String classValue = span.className();
            if (classValue.contains("fc") || classValue.contains("pc")) {
                this.addCoveredLine(Integer.parseInt(span.id().substring(1)));
            } else if (classValue.equals("nc")) {
                this.addMissedLines(Integer.parseInt(span.id().substring(1)));
            }
        }
    }
}
