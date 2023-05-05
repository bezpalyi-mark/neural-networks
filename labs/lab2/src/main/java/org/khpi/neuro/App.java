package org.khpi.neuro;

import org.khpi.neuro.model.AssociatedTable;
import org.khpi.neuro.model.Neuron;
import org.khpi.neuro.model.NeuronPair;
import org.khpi.neuro.model.NeuronTableConfig;
import org.khpi.neuro.model.Symbol;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class App {
    /**
     * 1...1
     * 11.11
     * 1.1.1
     * 1...1
     * 1...1
     */
    private static final List<Integer> M_STANDARD = List.of(1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1);

    /**
     * .111.
     * 1...1
     * 11111
     * 1...1
     * 1...1
     */
    private static final List<Integer> A_STANDARD = List.of(0, 1, 1, 1, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1);

    /**
     * 1...1
     * 11.11
     * 1...1
     * ....1
     * 1....
     */
    private static final List<Integer> TEST_M = List.of(1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0);

    /**
     * .1.1.
     * ....1
     * .1.1.
     * 1....
     * 1...1
     */
    private static final List<Integer> TEST_A = List.of(0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1);

    public static void main(String[] args) throws IOException {
        NeuronTableConfig config = LabReader.readConfig();

        System.out.println(config);

        NeuronTableService tableService = new NeuronTableService(config);

        System.out.println("SA Table: \n" + LabFormatter.getFormattedAssociatedTable(tableService.getSa()));
        System.out.println();
        System.out.println("AR Table: \n" + LabFormatter.getFormattedAssociatedTable(tableService.getAr()));

        Map<Symbol, Map<Neuron, Double>> aOutputs = new EnumMap<>(Symbol.class);

        aOutputs.put(Symbol.M, tableService.calculateAOutputSignals(M_STANDARD));
        aOutputs.put(Symbol.A, tableService.calculateAOutputSignals(A_STANDARD));

        double threshold = calculateThreshold(aOutputs.values());

        Map<Symbol, Double> rOutputs = new EnumMap<>(Symbol.class);

        rOutputs.put(Symbol.M, tableService.calculateROutputSignals(threshold, aOutputs.get(Symbol.M)));
        rOutputs.put(Symbol.A, tableService.calculateROutputSignals(threshold, aOutputs.get(Symbol.A)));

        NeuronPair neuronPair = NeuronPair.builder()
                .positive(M_STANDARD)
                .negative(A_STANDARD)
                .build();

        AssociatedTable<Neuron, Neuron> updatedARTable = tableService.adaptWeightsWithAlphaSupport(threshold, neuronPair);

        System.out.println(LabFormatter.getFormattedAOutputs(aOutputs));
        System.out.printf("Initial R outputs: M=%.2f A=%.2f %n%n", rOutputs.get(Symbol.M), rOutputs.get(Symbol.A));
        System.out.println("Updated AR table: \n" + LabFormatter.getFormattedAssociatedTable(updatedARTable));

        rOutputs.put(Symbol.M, tableService.calculateROutputSignals(threshold, aOutputs.get(Symbol.M)));
        rOutputs.put(Symbol.A, tableService.calculateROutputSignals(threshold, aOutputs.get(Symbol.A)));

        System.out.printf("Final R outputs: M=%.2f A=%.2f %n", rOutputs.get(Symbol.M), rOutputs.get(Symbol.A));

        // ----- TEST DATA -----
        Map<Neuron, Double> testMOutputsForANeurons = tableService.calculateAOutputSignals(TEST_M);
        Map<Neuron, Double> testAOutputsForANeurons = tableService.calculateAOutputSignals(TEST_A);
        double testThreshold = calculateThreshold(List.of(testMOutputsForANeurons, testAOutputsForANeurons));

        System.out.println("Output for TEST M: " + tableService.calculateROutputSignals(testThreshold, testMOutputsForANeurons));
        System.out.println("Output for TEST A: " + tableService.calculateROutputSignals(testThreshold, testAOutputsForANeurons));
    }

    private static double calculateThreshold(Collection<Map<Neuron, Double>> aInputs) {
        return aInputs.stream()
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .map(Map.Entry::getValue)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(Double.MIN_VALUE);
    }

}
