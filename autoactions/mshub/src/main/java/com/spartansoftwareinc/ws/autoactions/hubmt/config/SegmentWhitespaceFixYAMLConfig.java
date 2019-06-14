package com.spartansoftwareinc.ws.autoactions.hubmt.config;

public class SegmentWhitespaceFixYAMLConfig {

    private String regex;
    private Integer captureGroup;

    public SegmentWhitespaceFixYAMLConfig() {
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public Integer getCaptureGroup() {
        return captureGroup;
    }

    public void setCaptureGroup(Integer captureGroup) {
        this.captureGroup = captureGroup;
    }
}
