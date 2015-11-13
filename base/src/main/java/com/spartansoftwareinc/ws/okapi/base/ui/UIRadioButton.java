package com.spartansoftwareinc.ws.okapi.base.ui;

import java.io.IOException;

public class UIRadioButton implements UIElement {
    private String label, name;
    private Option defaultOption, alternateOption;

    public static class Option {
        private String label, value, extraHtml;
        private boolean isChecked;
        public Option(String label, String value, boolean isChecked) {
            this.label = label;
            this.value = value;
            this.isChecked = isChecked;
        }
        public Option(String label, String value, boolean isChecked, String extraHtml) {
            this(label, value, isChecked);
            this.extraHtml = extraHtml;
        }
    }

    public UIRadioButton(String label, String name, Option defaultOption, Option alternateOption) {
        this.label = label;
        this.name = name;
        this.defaultOption = defaultOption;
        this.alternateOption = alternateOption;
    }

    @Override
    public String render() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>");
        sb.append("<td class=\"prop_table_left_side\">" + UIUtil.escapeHtml(label) + ":</td>");
        sb.append("<td class=\"prop_table_right_side\">");
        sb.append(renderOption(defaultOption, 1));
        sb.append("<br/>");
        sb.append(renderOption(alternateOption, 2));
        sb.append("</tr>");
        return sb.toString();
    }

    private String renderOption(Option option, int optionNumber) {
        StringBuilder sb = new StringBuilder();
        String id = name + "_" + optionNumber;
        sb.append("<nobr>");
        sb.append("<input id=\"" + id + "\" name=\"" + name + "\" value=\"" + option.value);
        sb.append("\" type=\"radio\"");
        if (option.isChecked) {
            sb.append(" checked");
        }
        sb.append(">");
        sb.append("<label for=\"" + id + "\">");
        sb.append(UIUtil.escapeHtml(option.label));
        sb.append("</label>");
        if (option.extraHtml != null) {
            sb.append("&nbsp;&nbsp;");
            sb.append(option.extraHtml);
        }
        return sb.toString();
    }
}
