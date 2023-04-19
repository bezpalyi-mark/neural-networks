package org.khpi.neuro.service;

import org.khpi.neuro.model.Neuron;
import org.khpi.neuro.model.Symbol;
import org.khpi.neuro.model.SymbolData;

import java.util.Map;

public class NeuronTrainingService {

    private NeuronTrainingService() {}

    public static void trainNeuron(Neuron neuron, Map<Symbol, SymbolData> trainData) {
        trainData.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(neuron.getSymbol()))
                .map(Map.Entry::getValue)
                .forEach(neuron::correctWeightsToNegativeFor);

        trainData.entrySet().stream()
                .filter(entry -> entry.getKey().equals(neuron.getSymbol()))
                .map(Map.Entry::getValue)
                .forEach(neuron::correctWeightsToPositiveFor);
    }

    public static int getWeightsSize(Map<Symbol, SymbolData> trainData) {
        if (trainData.isEmpty()) {
            return 0;
        }

        SymbolData anyData = trainData.entrySet().stream()
                .findFirst()
                .orElseThrow()
                .getValue();

        boolean allTheSameSize = trainData.entrySet().stream()
                .allMatch(entry -> anyData.getData().size() == entry.getValue().getData().size());

        if (allTheSameSize) {
            return anyData.getData().size() + 1;
        } else {
            throw new IllegalStateException("Data for each Symbol must be with equal size");
        }
    }
}
