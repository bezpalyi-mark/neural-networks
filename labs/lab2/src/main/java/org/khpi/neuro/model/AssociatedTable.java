package org.khpi.neuro.model;

import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString
public class AssociatedTable<R, C> {
    private final Map<R, Map<C, Double>> tableData = new HashMap<>();

    public void addValue(R rowKey, C columnKey, double value) {
        Map<C, Double> columns = tableData.computeIfAbsent(rowKey, r -> new HashMap<>());
        columns.put(columnKey, value);
    }

    public Map<C, Double> getRow(R rowKey) {
        return tableData.get(rowKey);
    }

    public Map<R, Map<C, Double>> getRows() {
        return tableData;
    }
}
