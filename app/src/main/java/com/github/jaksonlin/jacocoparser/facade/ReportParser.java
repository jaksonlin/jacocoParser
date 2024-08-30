package com.github.jaksonlin.jacocoparser.facade;

import com.github.jaksonlin.jacocoparser.model.ClassCodeCoverage;
import com.github.jaksonlin.jacocoparser.model.JacocoReport;

import java.util.List;

public interface ReportParser {
    List<ClassCodeCoverage> GetClassCodeCoverages(String reportUrl) throws Exception;
}
class ReportParserImpl implements ReportParser{
    public List<ClassCodeCoverage> GetClassCodeCoverages(String reportUrl) throws Exception {
        JacocoReport report = JacocoReport.newJacocoReportFromURL(reportUrl);
        return report.collectClassCodeCoverage();
    }
}
