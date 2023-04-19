package org.khpi.neuro;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.khpi.neuro.model.Neuron;
import org.khpi.neuro.model.Symbol;
import org.khpi.neuro.model.SymbolData;
import org.khpi.neuro.service.CSVHandler;
import org.khpi.neuro.service.NeuronTrainingService;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class App {

    private static final String FILE_OPTION = "f";

    public static void main(String[] args) throws ParseException {
        List<Symbol> symbols = List.of(Symbol.A, Symbol.K, Symbol.M, Symbol.R);

        Map<Symbol, SymbolData> allData = new EnumMap<>(Symbol.class);
        symbols.forEach(symbol -> allData.put(symbol, CSVHandler.readSymbolData(symbol)));

        System.out.printf("Standards loaded: %s%n", symbols);

        List<Neuron> neurons = new ArrayList<>();
        int weightsSize = NeuronTrainingService.getWeightsSize(allData);

        allData.keySet().stream()
                .map(symbol -> Neuron.builder()
                        .weightsSize(weightsSize)
                        .symbol(symbol)
                        .build())
                .forEach(neurons::add);

        neurons.forEach(neuron -> NeuronTrainingService.trainNeuron(neuron, allData));

        System.out.println("Training complete!");

        Options options = new Options();
        options.addOption(FILE_OPTION, true, "file name");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);


        if (cmd.hasOption(FILE_OPTION)) {
            SymbolData inputData = CSVHandler.readSymbolData(cmd.getOptionValue(FILE_OPTION));

            AbstractMap.SimpleImmutableEntry<Symbol, Integer> result = neurons.stream()
                    .map(neuron -> new AbstractMap.SimpleImmutableEntry<>(neuron.getSymbol(), neuron.calculateNeuronSignals(inputData.getData())))
                    .max(Map.Entry.comparingByValue())
                    .orElse(new AbstractMap.SimpleImmutableEntry<>(null, -1));

            System.out.println("Your symbol is: " + result.getKey());
        } else {
            throw new IllegalArgumentException("You need specify -f option with file name");
        }
    }
}
