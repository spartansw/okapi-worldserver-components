package com.spartansoftwareinc.ws.okapi.mt.base;

import com.idiominc.wssdk.component.mt.WSMTRequest;

public class WSMTRequestStabs {

    public static WSMTRequest[] getWSMTRequestStabs(String... segments) {
        if (segments == null || segments.length == 0) {
            return new WSMTRequest[]{};
        }

        WSMTRequest[] requests = new WSMTRequest[segments.length];
        for (int i = 0; i < segments.length; i++) {
            requests[i] = new WSMTRequestStab(segments[i]);
        }

        return requests;
    }
}
