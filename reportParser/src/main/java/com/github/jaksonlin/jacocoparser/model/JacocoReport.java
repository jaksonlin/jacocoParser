package com.github.jaksonlin.jacocoparser.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.github.jaksonlin.jacocoparser.util.Util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;


public class JacocoReport {
    final private String initializeUrl;

    private boolean hasLoadedSubReport = false;
    private String reportTitle;
    private List<JacocoReportRecord> reportContent = new ArrayList<>();

    private JacocoReport(String reportTitle, String initializeUrl) {
        this.reportTitle = reportTitle;
        this.initializeUrl = initializeUrl;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public List<JacocoReportRecord> getReportContent() {
        return reportContent;
    }

    public void setReportContent(List<JacocoReportRecord> reportContent) {
        this.reportContent = reportContent;
    }

    // this will only load the top level report table, use loadSubReports to load all sub reports
    public static JacocoReport newJacocoReportFromURL(String url) throws Exception {

        byte[] body = Util.DownloadWebPage(url);

        Document doc = Jsoup.parse(new String(body));
        Element title = doc.select("h1").first();
        if (title == null) {
            throw new Exception("title not found");
        }
        String reportTitle = title.text();
        JacocoReport report = new JacocoReport(reportTitle, url);

        Element table = doc.select("table").first();
        if (table == null) {
            throw new Exception("table not found");
        }
        report.setReportContent(createJacocoReportRecords(table));
        return report;

    }

    // extract the content element from the table
    private static ArrayList<JacocoReportRecord>  createJacocoReportRecords(Element table) {
        ArrayList<JacocoReportRecord> records = new ArrayList<>();
        int recordIndex = 0;
        int tableColIndex = 0;
        for (Element row : table.select("tr")) {
            // header跳过
            if (recordIndex == 0) {
                recordIndex += 1;
                continue;
            }
            Elements tds = row.select("td");
            if (tds.isEmpty()) {
                continue;
            }
            JacocoReportRecord record = new JacocoReportRecord();
            for (Element td : tds) {

                String text = td.text();
                switch (tableColIndex) {
                    case 0:
                        record.setElement(text);
                        // check if there's <a> in this td, if yes, extract the href
                        Elements links = td.select("a");
                        if (!links.isEmpty()) {
                            record.setHref(links.get(0).attr("href"));
                        }
                        break;
                    case 2:
                        record.setInstructCoverage(text);
                        break;
                    case 4:
                        record.setBranchCoverage(text);
                        break;
                    case 5:
                        record.setMissedBranch(text);
                        break;
                    case 6:
                        record.setComplexity(text);
                        break;
                    case 7:
                        record.setMissedLine(text);
                        break;
                    case 8:
                        record.setNumberOfLine(text);
                        break;
                    case 9:
                        record.setMissedMethod(text);
                        break;
                    case 10:
                        record.setNumberOfMethod(text);
                        break;
                    case 11:
                        record.setMissedClass(text);
                        break;
                    case 12:
                        record.setNumberOfClass(text);
                        break;
                }
                tableColIndex += 1;
                if (tableColIndex == tds.size()) {
                    recordIndex += 1;
                    tableColIndex = 0;
                    record.convertNumericFields();
                    records.add(record);
                }
            }
        }
        return records;
    }

    // navigate the link in the reportContent and load the subReport
    public void loadSubReports() {

        if (this.hasLoadedSubReport) {
            return;
        }

        if (this.reportContent.isEmpty()) {
            return;
        }

        String url = this.initializeUrl;

        if (url.endsWith("index.html")) {
            url = url.substring(0, url.length() - "index.html".length());
        }

        // bfs queue for the job
        ConcurrentLinkedQueue<JacocoReportRecord> jobList = new ConcurrentLinkedQueue<>();
        for (JacocoReportRecord record : reportContent) {
            if (!record.getHref().isEmpty() && !record.getHref().contains("#")) {
                record.setUrl(url + record.getHref());
                jobList.add(record);
            }
        }


        for (JacocoReportRecord record : jobList) {

            JacocoReport subReport = null;
            try {
                subReport = newJacocoReportFromURL(record.getUrl());
            } catch (Exception e) {
                System.out.printf("download report for %s, failed, link: %s\n", record.getHref(), e.getMessage());
                continue;
            }

            record.setSubReport(subReport);


            for (JacocoReportRecord subRecord : subReport.getReportContent()) {
                // stop collection when we reach the java file level, the analysis for java file is done in ClassCodeCoverage
                if (!subRecord.getHref().isEmpty()) {
                    if (!subRecord.getHref().contains("#")){
                        String urlBase = record.getUrl();
                        if (urlBase.contains("index.html")) {
                            urlBase = urlBase.substring(0, urlBase.length() - "index.html".length());
                        }
                        subRecord.setUrl(urlBase + subRecord.getHref());
                        jobList.add(subRecord);
                    } else {
                        String urlBase = record.getUrl().replace(record.getHref(), "");
                        subRecord.setUrl(urlBase + subRecord.getHref());
                    }
                }
            }
        }
        this.hasLoadedSubReport = true;
    }

    // generate a list of ClassCodeCoverage from the reportContent
    public List<ClassCodeCoverage> collectClassCodeCoverage() throws Exception {

        // make sure we have loaded the sub reports
        this.loadSubReports();

        List<ClassCodeCoverage> result = new ArrayList<>();
        // initialize the job list for breadth first search
        ConcurrentLinkedQueue<JacocoReportRecord> jobList = new ConcurrentLinkedQueue<>(this.reportContent);

        HashMap<String, Boolean> added = new HashMap<>();
        for (JacocoReportRecord record : jobList) {

            Optional<JacocoReport> subReport = record.getSubReport();
            if (!subReport.isPresent()){
                continue;
            }
            for (JacocoReportRecord subRecord : subReport.get().getReportContent()) {
                if (!subRecord.getHref().isEmpty()) {
                    if (!subRecord.getHref().contains("#")){
                        jobList.add(subRecord);
                    } else {
                        Optional<String> classFullName = subRecord.getRecordFullyQualifiedClassName();
                        if (classFullName.isPresent() && !added.containsKey(classFullName.get())) {
                            added.put(classFullName.get(), true);
                            ClassCodeCoverage cov = new ClassCodeCoverage(subRecord);
                            result.add(cov);
                        }
                    }
                }
            }
        }

        for (ClassCodeCoverage c : result) {
            c.loadCoverage();
        }

        return result;
    }

}
