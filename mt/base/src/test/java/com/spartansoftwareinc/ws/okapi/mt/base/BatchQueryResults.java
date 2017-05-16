package com.spartansoftwareinc.ws.okapi.mt.base;

import net.sf.okapi.common.query.QueryResult;
import net.sf.okapi.common.resource.TextFragment;

import java.util.ArrayList;
import java.util.List;

public class BatchQueryResults {

    public static List<List<QueryResult>> getBatchQueryResults(String... source) {
        List<List<QueryResult>> queryResultsList = new ArrayList<>();

        for (String s : source) {
            List<QueryResult> queryResults = new ArrayList<>();
            final QueryResult qr = new QueryResult();
            qr.source = new TextFragment(s);
            qr.target = new TextFragment(s);
            queryResults.add(qr);
            queryResultsList.add(queryResults);
        }

        return queryResultsList;
    }
}
