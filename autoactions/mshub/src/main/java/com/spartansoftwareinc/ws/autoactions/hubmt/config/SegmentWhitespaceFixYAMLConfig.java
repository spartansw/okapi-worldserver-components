package com.spartansoftwareinc.ws.autoactions.hubmt.config;

public class SegmentWhitespaceFixYAMLConfig {

    private String regex;
    private Integer leftCaptureGroup;
    private Integer leftIgnoreCaptureGroup;
    private Integer centerCaptureGroup;
    private Integer compareCaptureGroup;
    private Integer rightIgnoreCaptureGroup;
    private Integer rightCaptureGroup;

    public SegmentWhitespaceFixYAMLConfig() {
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public Integer getLeftCaptureGroup() {
        return leftCaptureGroup;
    }

    public void setLeftCaptureGroup(Integer leftCaptureGroup) {
        this.leftCaptureGroup = leftCaptureGroup;
    }

    public Integer getLeftIgnoreCaptureGroup() {
        return leftIgnoreCaptureGroup;
    }

    public void setLeftIgnoreCaptureGroup(Integer leftIgnoreCaptureGroup) {
        this.leftIgnoreCaptureGroup = leftIgnoreCaptureGroup;
    }

    public Integer getCenterCaptureGroup() {
        return centerCaptureGroup;
    }

    public void setCenterCaptureGroup(Integer centerCaptureGroup) {
        this.centerCaptureGroup = centerCaptureGroup;
    }


    public Integer getCompareCaptureGroup() {
        return compareCaptureGroup;
    }

    public void setCompareCaptureGroup(Integer compareCaptureGroup) {
        this.compareCaptureGroup = compareCaptureGroup;
    }

    public Integer getRightIgnoreCaptureGroup() {
        return rightIgnoreCaptureGroup;
    }

    public void setRightIgnoreCaptureGroup(Integer rightIgnoreCaptureGroup) {
        this.rightIgnoreCaptureGroup = rightIgnoreCaptureGroup;
    }

    public Integer getRightCaptureGroup() {
        return rightCaptureGroup;
    }

    public void setRightCaptureGroup(Integer rightCaptureGroup) {
        this.rightCaptureGroup = rightCaptureGroup;
    }


}
