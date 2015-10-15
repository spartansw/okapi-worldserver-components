package com.spartansoftwareinc.ws.okapi.base.ui;

import java.io.IOException;

public class UICheckbox implements UIElement {
    private String label, inputName;
    private boolean value;

    public UICheckbox(String label, String inputName, boolean value) {
        this.label = label;
        this.inputName = inputName;
        this.value = value;
    }

    @Override
    public String render() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>");
        sb.append("<td class=\"prop_table_left_side\">" + UIUtil.escapeHtml(label) + ":</td>");
        sb.append("<td class=\"prop_table_right_side\">");
        sb.append("<nobr><input name=\"");
        sb.append(inputName);
        sb.append("\" value=\"on\" type=\"checkbox\"");
        if (value) {
            sb.append(" checked");
        }
        sb.append("></nobr></td>");
        sb.append("</tr>");
        return sb.toString();        
    }
}
