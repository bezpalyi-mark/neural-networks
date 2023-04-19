package org.khpi.neuro.service;

import org.khpi.neuro.model.Symbol;
import org.khpi.neuro.model.SymbolData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVHandler {
    private static final String COMMA_DELIMITER = ",";

    private CSVHandler() {}

    public static SymbolData readSymbolData(Symbol symbol) {
        String path = String.format("/standards/%S.txt", symbol);

        try (InputStream stream = CSVHandler.class.getResourceAsStream(path)) {

            return readSymbolData(stream);

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static SymbolData readSymbolData(String filePath) {
        String path = String.format(filePath);

        try (InputStream stream = CSVHandler.class.getResourceAsStream(path)) {

            return readSymbolData(stream);

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static SymbolData readSymbolData(InputStream inputStream) {
        List<Integer> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                Arrays.stream(values)
                        .map(Integer::parseInt)
                        .forEach(data::add);
            }
        } catch (Exception e) {
            System.err.println("Cannot load data");
            return null;
        }

        return new SymbolData(data);
    }
}
