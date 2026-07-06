package com.back.standard.recommend.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Vector {

    public record VectorElement(
            long label,
            float value
    ) { }

    private final Map<Long, Float> values = new HashMap<>();
    private float sum = 0;

    public Set<Long> getLabels() {
        return values.keySet();
    }

    public List<VectorElement> getVectorElementList() {
        return values.entrySet().stream()
                .map(entry ->
                        new VectorElement(entry.getKey(), entry.getValue()))
                .toList();
    }

    public void putValue(long label, float value) {
        if (values.containsKey(label)) {
            sum -= values.get(label);
        }
        values.put(label, value);
        sum += value;
    }

    public Vector subtractionValue(float value) {
        values.replaceAll((key, oldValue) -> oldValue - value);
        return this;
    }

    public float getAverageValue() {
        return sum / values.size();
    }

    public boolean isEmpty() { return values.isEmpty(); }

}
