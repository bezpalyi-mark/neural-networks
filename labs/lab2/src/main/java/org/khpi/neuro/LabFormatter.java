package org.khpi.neuro;

import org.khpi.neuro.model.AssociatedTable;
import org.khpi.neuro.model.Neuron;
import org.khpi.neuro.model.Symbol;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LabFormatter {

    private LabFormatter() {}

    public static String getFormattedAOutputs(Map<Symbol, Map<Neuron, Double>> aOutputs) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("A outputs: \n");

        for (Map.Entry<Symbol, Map<Neuron, Double>> symbolMapEntry : aOutputs.entrySet()) {
            stringBuilder.append("Symbol ")
                    .append(symbolMapEntry.getKey())
                    .append(" { | ");

            List<Map.Entry<Neuron, Double>> entryList = symbolMapEntry.getValue().entrySet().stream()
                    .sorted(Comparator.comparing(entry -> entry.getKey().getId()))
                    .collect(Collectors.toList());

            for (Map.Entry<Neuron, Double> entry : entryList) {
                stringBuilder.append("Neuron #")
                        .append(entry.getKey().getId())
                        .append(" Value: ")
                        .append(String.format("%.2f", entry.getValue()))
                        .append(" | ");
            }

            stringBuilder.append("}\n");
        }

        return stringBuilder.toString();
    }

    public static String getFormattedAssociatedTable(AssociatedTable<Neuron, Neuron> table) {
        StringBuilder stringBuilder = new StringBuilder();

        List<Map.Entry<Neuron, Map<Neuron, Double>>> sortedRows = table.getRows().entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().getId()))
                .collect(Collectors.toList());

        for (Map.Entry<Neuron, Map<Neuron, Double>> rowWithColumns : sortedRows) {
            Neuron rowNeuron = rowWithColumns.getKey();
            stringBuilder.append("Row Element: ")
                    .append(rowNeuron.getType())
                    .append(rowNeuron.getId())
                    .append(" { | ");

            List<Map.Entry<Neuron, Double>> sortedColumns = rowWithColumns.getValue().entrySet().stream()
                    .sorted(Comparator.comparing(entry -> entry.getKey().getId()))
                    .collect(Collectors.toList());

            for (Map.Entry<Neuron, Double> column: sortedColumns) {
                stringBuilder.append(column.getKey().getType())
                        .append(column.getKey().getId())
                        .append(":")
                        .append(String.format("%.2f", column.getValue()))
                        .append(" | ");
            }

            stringBuilder.append("}\n");
        }

        return stringBuilder.toString();
    }
}
