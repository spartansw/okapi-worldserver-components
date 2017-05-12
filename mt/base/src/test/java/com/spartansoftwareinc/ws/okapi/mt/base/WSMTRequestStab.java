package com.spartansoftwareinc.ws.okapi.mt.base;

import com.idiominc.wssdk.component.mt.WSMTRequest;
import com.idiominc.wssdk.mt.WSMTResult;

public class WSMTRequestStab implements WSMTRequest {

    private final String source;
    private WSMTResult[] wsmtResults;

    public WSMTRequestStab(String source) {
        this.source = source;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public void setResults(WSMTResult[] wsmtResults) {
        this.wsmtResults = wsmtResults;
    }

    @Override
    public WSMTResult[] getMTResults() {
        return wsmtResults;
    }

    @Override
    public int getScore() {
        return 0;
    }

    @Override
    public boolean isRepetition() {
        return false;
    }
}

