package com.spartansoftwareinc.ws.okapi.base.ui;

public class UITextArea implements UIElement {
    private String label, inputName, value;
    private Integer rows, cols;

    public UITextArea(String label, String inputName, String initValue) {
        this.label = label;
        this.inputName = inputName;
        this.value = initValue != null ? initValue : "";
    }

    /**
     * The method to specify the size of the text area by the number of rows and columns.
     * @param rows the number of rows
     * @param cols the number of columns
     * @return the object itself after modification
     */
    public UITextArea setSize(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        return this;
    }

    @Override
    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>")
          .append("<td class=\"prop_table_left_side\">" + UIUtil.escapeHtml(label) + ":</td>")
          .append("<td class=\"prop_table_right_side\">")
          .append("<textarea name=\"").append(UIUtil.escapeHtml(inputName)).append("\"");
        appendPositiveIntAttr(sb, rows, "rows");
        appendPositiveIntAttr(sb, cols, "cols");
        sb.append("\"/>")
          .append(UIUtil.escapeHtml(value))
          .append("</textarea>")
          .append("</td>")
          .append("</tr>");
        return sb.toString();
    }

    private void appendPositiveIntAttr(StringBuilder sb, Number num, String attrName) {
        if (num != null && num.intValue() > 0) {
            sb.append(" ").append(attrName).append("=")
              .append("\"").append(num.toString()).append("\"");
        }
    }
}
