package com.spartansoftwareinc.ws.okapi.base.ui;

public class UITextField implements UIElement {
    private String label, inputName, value;

    public UITextField(String label, String inputName, String value) {
        this.label = label;
        this.inputName = inputName;
        this.value = value != null ? value : "";
    }

    @Override
    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>");
        sb.append("<td class=\"prop_table_left_side\">" + UIUtil.escapeHtml(label) + ":</td>");
        sb.append("<td class=\"prop_table_right_side\">");
        sb.append("<input type=\"text\" name=\"" + UIUtil.escapeHtml(inputName) +
                  "\" value=\"" + UIUtil.escapeHtml(value) + "\"/>");
        sb.append("</td>");
        sb.append("</tr>");
        return sb.toString();
    }

}
