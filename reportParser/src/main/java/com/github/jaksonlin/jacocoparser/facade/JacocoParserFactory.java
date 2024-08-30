package com.github.jaksonlin.jacocoparser.facade;


public class JacocoParserFactory {
    public static IJacocoReportParser NewHtmlReportParser() {
        return new JacocoHtmlReportParserImpl();
    }
}
