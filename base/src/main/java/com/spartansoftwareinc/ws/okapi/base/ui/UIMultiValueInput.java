package com.spartansoftwareinc.ws.okapi.base.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class UIMultiValueInput implements UIElement {
    private Collection<String> options = new ArrayList<>();
    private Collection<String> selected = new ArrayList<>();
    private String label, inputName;

    public UIMultiValueInput(String label, String inputName, Collection<String> options, Collection<String> selected) {
        this.label = label;
        this.inputName = inputName;
        this.options = options;
        this.selected = selected;
    }

    @Override
    public String render() throws IOException {
        String s = UIUtil.loadResourceAsString("/keyConfiguration.html.template");
        s = s.replaceAll("\\$\\{label\\}", UIUtil.escapeHtml(label));
        s = s.replaceAll("\\$\\{inputName\\}", inputName);
        return String.format(s, getOptionValues(), getSelectedValues());
    }

    private String getOptionValues() {
        StringBuilder sb = new StringBuilder();
        for (String option : options) {
            option = UIUtil.escapeHtml(option.trim());
            if (!option.isEmpty()) {
                sb.append("<option value=\"")
                  .append(option)
                  .append("\">")
                  .append(UIUtil.escapeHtml(option))
                  .append("</option>");
            }
        }
        return sb.toString();
    }

    private String getSelectedValues() {
        StringBuilder sb = new StringBuilder();
        Iterator<String> iterator = selected.iterator();
        if (iterator.hasNext()) {
            sb.append(UIUtil.escapeHtml(iterator.next()));
        }
        while (iterator.hasNext()) {
            sb.append(",");
            sb.append(UIUtil.escapeHtml(iterator.next()));
        }
        return sb.toString();
    }

}
