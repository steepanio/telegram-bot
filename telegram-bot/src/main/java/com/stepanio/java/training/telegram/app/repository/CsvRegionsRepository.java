package com.stepanio.java.training.telegram.app.repository;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CsvRegionsRepository {

    public Map<String, String> readRows(String csvFilePath) {
        InputStream inputStream = getClass().getResourceAsStream(csvFilePath);

        if (inputStream == null) {
            return new HashMap<>();
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            List<String> lines = reader.lines().toList();
            if (!lines.isEmpty()) {
                return lines.stream()
                        .skip(1)
                        .map(line -> line.split(";"))
                        .collect(Collectors.toMap(
                                parts -> parts[0],
                                parts -> parts[1]
                        ));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new HashMap<>();
    }
}
