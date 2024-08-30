package com.github.jaksonlin.jacocoparser.facade;


public class Factory {
    public static ReportParser NewReportParser() {
        return new ReportParserImpl();
    }
}
