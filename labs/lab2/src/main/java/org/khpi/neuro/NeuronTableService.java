package org.khpi.neuro;

import org.khpi.neuro.model.AssociatedTable;
import org.khpi.neuro.model.Neuron;
import org.khpi.neuro.model.NeuronPair;
import org.khpi.neuro.model.NeuronTableConfig;
import org.khpi.neuro.model.NeuronType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class NeuronTableService {

    private final Random random = new Random();

    private final double rangeMin;
    private final double rangeMax;

    private final List<Neuron> sNeurons = new ArrayList<>();
    private final List<Neuron> aNeurons = new ArrayList<>();

    private final AssociatedTable<Neuron, Neuron> sa;
    private final AssociatedTable<Neuron, Neuron> ar;

    private final Neuron r = Neuron.builder()
            .id(1)
            .type(NeuronType.R)
            .build();

    public NeuronTableService(NeuronTableConfig config) {
        this.rangeMin = config.getRangeMin();
        this.rangeMax = config.getRangeMax();

        initNeurons(config);

        sa = createRandomTable(aNeurons, sNeurons);
        ar = createRandomTable(Collections.singletonList(r), aNeurons);
    }

    private void initNeurons(NeuronTableConfig config) {
        for (int i = 0; i < config.getSNeuronsSize(); i++) {
            sNeurons.add(Neuron.builder()
                    .id(i + 1)
                    .type(NeuronType.S)
                    .build());
        }

        for (int i = 0; i < config.getANeuronsSize(); i++) {
            aNeurons.add(Neuron.builder()
                    .id(i + 1)
                    .type(NeuronType.A)
                    .build());
        }
    }

    private AssociatedTable<Neuron, Neuron> createRandomTable(List<Neuron> rowNeurons, List<Neuron> columnNeurons) {
        AssociatedTable<Neuron, Neuron> table = new AssociatedTable<>();

        for (Neuron rowNeuron : rowNeurons) {
            for (Neuron columnNeuron : columnNeurons) {
                double data = rangeMin + (rangeMax - rangeMin) * random.nextDouble();
                table.addValue(rowNeuron, columnNeuron, data);
            }
        }

        return table;
    }

    public Map<Neuron, Double> calculateAOutputSignals(List<Integer> inputSymbolData) {
        Map<Neuron, Double> aOutputs = new HashMap<>();

        for (Neuron a : aNeurons) {
            Map<Neuron, Double> row = sa.getRow(a);

            double aOut = 0;

            for (int i = 0; i < sNeurons.size(); i++) {
                Integer n = inputSymbolData.get(i);
                Neuron s = sNeurons.get(i);

                Double value = row.get(s);
                aOut += value * n;
            }

            aOutputs.put(a, aOut);
        }

        return aOutputs;
    }

    public Double calculateROutputSignals(Double threshold, Map<Neuron, Double> aOutputs) {
        List<Integer> activeIds = getActiveIds(threshold, aOutputs);

        return ar.getRow(r).entrySet().stream()
                .filter(entry -> activeIds.contains(entry.getKey().getId()))
                .map(Map.Entry::getValue)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    public AssociatedTable<Neuron, Neuron> adaptWeightsWithAlphaSupport(double threshold, NeuronPair neuronPair) {
        Map<Neuron, Double> rRow = ar.getRow(r);

        Map<Neuron, Double> positiveAOutputs = calculateAOutputSignals(neuronPair.getPositive());
        Map<Neuron, Double> negativeAOutputs = calculateAOutputSignals(neuronPair.getNegative());

        List<Integer> positiveActiveNeuronIds = getActiveIds(threshold, positiveAOutputs);
        List<Integer> negativeActiveNeuronIds = getActiveIds(threshold, negativeAOutputs);

        List<Neuron> activePositiveNeurons = rRow.keySet().stream()
                .filter(aDouble -> positiveActiveNeuronIds.contains(aDouble.getId()))
                .collect(Collectors.toList());

        List<Neuron> activeNegativeNeurons = rRow.keySet().stream()
                .filter(aDouble -> negativeActiveNeuronIds.contains(aDouble.getId()))
                .collect(Collectors.toList());

        boolean positiveNext = true;
        double positiveROutput = calculateROutputSignals(threshold, positiveAOutputs);
        double negativeROutput = calculateROutputSignals(threshold, negativeAOutputs);
        double alphaThreshold = (positiveROutput + negativeROutput) / 2.0;

        System.out.println("Alpha threshold: " + alphaThreshold);

        while (positiveROutput < alphaThreshold || negativeROutput >= alphaThreshold) {

            if (positiveNext && positiveROutput < alphaThreshold) {
                correctRTableForPositive(activePositiveNeurons);

            } else if (!positiveNext && negativeROutput >= alphaThreshold) {
                correctRTableForNegative(activeNegativeNeurons);
            }

            positiveNext = !positiveNext;

            positiveROutput = calculateROutputSignals(threshold, positiveAOutputs);
            negativeROutput = calculateROutputSignals(threshold, negativeAOutputs);
        }

        return ar;
    }

    private void correctRTableForPositive(List<Neuron> activePositiveNeurons) {
        Map<Neuron, Double> rRow = ar.getRow(r);

        activePositiveNeurons.forEach(neuron -> {
            Double value = rRow.get(neuron);

            if (value < 1) {
                rRow.put(neuron, value + 0.1);
            }
        });
    }

    private void correctRTableForNegative(List<Neuron> activeNegativeNeurons) {
        Map<Neuron, Double> rRow = ar.getRow(r);

        activeNegativeNeurons.forEach(neuron -> {
            Double value = rRow.get(neuron);

            if (value > 0) {
                rRow.put(neuron, value - 0.1);
            }
        });
    }

    private List<Integer> getActiveIds(Double threshold, Map<Neuron, Double> neurons) {
        return neurons.entrySet().stream()
                .filter(entry -> isActive(threshold, entry))
                .map(entry -> entry.getKey().getId())
                .collect(Collectors.toList());
    }

    private boolean isActive(Double threshold, Map.Entry<Neuron, Double> neuron) {
        return neuron.getValue() >= threshold;
    }

    public AssociatedTable<Neuron, Neuron> getSa() {
        return sa;
    }

    public AssociatedTable<Neuron, Neuron> getAr() {
        return ar;
    }
}
