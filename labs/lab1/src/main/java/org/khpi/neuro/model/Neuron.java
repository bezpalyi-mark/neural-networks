package org.khpi.neuro.model;

import lombok.Builder;
import lombok.Value;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Value
public class Neuron {
    int[] weights;

    @Builder.Default
    int iterationLimit = 100000;
    Symbol symbol;

    @Builder
    public Neuron(int weightsSize, Symbol symbol) {
        this.weights = new int[weightsSize];
        this.symbol = symbol;
    }

    public void correctWeightsToNegativeFor(SymbolData symbolData) {
        int currentOutput = calculateNeuronSignals(symbolData.getData());

        while (currentOutput >= 0) {
            updateWeights(symbolData.getData(), -1, Collections.emptyList());
            currentOutput = calculateNeuronSignals(symbolData.getData());
        }
    }

    public void correctWeightsToPositiveFor(SymbolData symbolData) {
        int currentOutput = calculateNeuronSignals(symbolData.getData());

        while (currentOutput <= 0) {
            updateWeights(symbolData.getData(), 1, Collections.emptyList());
            currentOutput = calculateNeuronSignals(symbolData.getData());
        }
    }

    public void teach(LearningPair learningPair) {
        List<Integer> x1Data = learningPair.getPositiveSymbolData().getData();
        List<Integer> x2Data = learningPair.getNegativeSymbolData().getData();

        int t1 = learningPair.getPositiveValue();
        int t2 = learningPair.getNegativeValue();

        int y1 = calculateNeuronSignals(x1Data);
        int y2 = calculateNeuronSignals(x2Data);

        int limit = iterationLimit;

        while (y1 <= y2 && limit > 0) {
            updateWeights(x1Data, t1, Collections.emptyList());

            List<Integer> indexesOfDifference = new ArrayList<>();
            for (int i = 0; i < x1Data.size(); i++) {
                if (!x1Data.get(i).equals(x2Data.get(i))) {
                    indexesOfDifference.add(i);
                }
            }

            updateWeights(x2Data, t2, indexesOfDifference);

            y1 = calculateNeuronSignals(x1Data);
            y2 = calculateNeuronSignals(x2Data);

            limit--;
        }
    }

    public int calculateNeuronSignals(List<Integer> input) {
        int sum = 0;

        for (int i = 0; i < input.size(); i++) {
            sum += input.get(i) * weights[i + 1] + weights[0];
        }

        return sum;
    }

    private void updateWeights(List<Integer> input, int t, List<Integer> mandatoryIndexes) {
        //x0 = 1
        weights[0] = weights[0] + t;

        List<Integer> unvisitedIndexes = IntStream.range(0, input.size())
                .boxed()
                .collect(Collectors.toList());

        do {
            Integer i = unvisitedIndexes.get(0);

            int element = input.get(i);
            int newWeight = weights[i + 1] + element * t;
            weights[i + 1] = newWeight;

            //setting weights that should contain the same value
            IntStream.range(0, input.size())
                    //finding only indexes with the same element value
                    .filter(index -> input.get(index) == element)
                    //filtering out current index
                    .filter(index -> index != i)
                    //filtering out already calculated indexes
                    .filter(unvisitedIndexes::contains)
                    .forEach(index -> {
                        //if the field is NOT mandatory to calculate and mandatory fields NOT containing current index
                        //second options checking that if we calculated mandatory field then we don't want to set this value to others
                        if (!mandatoryIndexes.contains(index) && !mandatoryIndexes.contains(i)) {
                            weights[index + 1] = newWeight;
                            unvisitedIndexes.remove(Integer.valueOf(index));
                        }
                    });

            unvisitedIndexes.remove(i);

        } while (CollectionUtils.isNotEmpty(unvisitedIndexes));
    }
}
