package com.spartansoftwareinc.ws.okapi.base.ui;

import java.util.ArrayList;
import java.util.List;

import com.idiominc.wssdk.WSRuntimeException;

public class UITable {
    private List<UIElement> elements = new ArrayList<UIElement>();

    public UITable() {
    }

    public UITable add(UIElement element) {
        elements.add(element);
        return this;
    }

    public String render() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("<table cellpadding=\"3\" cellspacing=\"1\" border=\"0\">");
            for (UIElement element : elements) {
                sb.append(element.render());
            }
            sb.append("</table>");
            return sb.toString();
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new WSRuntimeException(e);
        }
    }
}
