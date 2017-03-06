package com.spartansoftwareinc.ws.okapi.base.ui;

public class UITextField implements UIElement {
    private String label, inputName, value;
    private Integer length;

    public UITextField(String label, String inputName, String value) {
        this.label = label;
        this.inputName = inputName;
        this.value = value != null ? value : "";
    }

    public UITextField setSize(int length) {
        this.length = length;
        return this;
    }

    @Override
    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>");
        sb.append("<td class=\"prop_table_left_side\">" + UIUtil.escapeHtml(label) + ":</td>");
        sb.append("<td class=\"prop_table_right_side\">");
        sb.append("<input type=\"text\" name=\"")
                    .append(UIUtil.escapeHtml(inputName))
                    .append("\" value=\"")
                    .append(UIUtil.escapeHtml(value))
                    .append("\"");
        if (length != null && length > 0) {
            sb.append(" size=\"")
              .append(String.valueOf(length))
              .append("\"");
        }
        sb.append("\"/>");
        sb.append("</td>");
        sb.append("</tr>");
        return sb.toString();
    }

}
