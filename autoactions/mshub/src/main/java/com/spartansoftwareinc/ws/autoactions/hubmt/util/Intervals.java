package com.spartansoftwareinc.ws.autoactions.hubmt.util;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Intervals {

    private final NavigableMap<Integer, Integer> intervals;

    public Intervals() {
        intervals = new TreeMap<>();
    }

    public void addInterval(int min, int max) {
        final Map.Entry<Integer, Integer> minInterval = getInterval(min);
        final Map.Entry<Integer, Integer> maxInterval = getInterval(max);
        if(minInterval != null){
            min = minInterval.getKey();
            intervals.remove(minInterval.getKey());
        }
        if(maxInterval != null){
            max = maxInterval.getValue();
            intervals.remove(maxInterval.getKey());
        }
       intervals.put(min, max);
    }

    public Map.Entry<Integer, Integer>  getInterval(int value){
        Map.Entry<Integer, Integer> entry = intervals.floorEntry(value);
        return entry != null && entry.getKey() <= value && value <= entry.getValue() ? entry : null;
    }

    public boolean containsInterval(int value){
        return getInterval(value) != null;
    }

    public boolean containsInterval(int start, int end) {

        final Map.Entry<Integer, Integer> minInterval = intervals.floorEntry(end);

        if (minInterval != null && minInterval.getValue() >= start) {
            return true;
        }

        final Map.Entry<Integer, Integer> maxInterval = intervals.ceilingEntry(start);

        return maxInterval != null && maxInterval.getKey() <= end;
    }
}
