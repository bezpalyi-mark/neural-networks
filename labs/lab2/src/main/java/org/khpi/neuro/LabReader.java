package org.khpi.neuro;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.khpi.neuro.model.NeuronTableConfig;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LabReader {

    private static final Gson GSON = new Gson();

    private LabReader() {}

    public static NeuronTableConfig readConfig() throws IOException {
        String jsonConfig = IOUtils.toString(
                Objects.requireNonNull(LabReader.class.getResourceAsStream("/config/config.json")), StandardCharsets.UTF_8);
        return GSON.fromJson(JsonParser.parseString(jsonConfig), NeuronTableConfig.class);
    }

    public static List<Integer> readSymbolData(String path) throws IOException {
        List<Integer> data = new ArrayList<>();

        String jsonData = IOUtils.toString(URI.create(path), StandardCharsets.UTF_8);
        String[] lines = jsonData.split(System.lineSeparator());

        for (String line : lines) {
            for (char c : line.toCharArray()) {
                data.add(Integer.parseInt(String.valueOf(c)));
            }
        }

        return data;
    }
}
