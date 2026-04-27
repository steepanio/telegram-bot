package com.stepanio.java.training.telegram.app.repository;

import com.stepanio.java.training.telegram.app.exception.ReadFileException;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
                Map<String, String> map = new LinkedHashMap<>();
                boolean first = true;
                for (String line : lines) {
                    if (first) {
                        first = false;
                        continue;
                    }
                    String[] parts = line.split(";");
                    if (map.put(parts[0], parts[1]) != null) {
                        throw new IllegalStateException("Duplicate key");
                    }
                }
                return map;
            }
        } catch (IOException e) {
            throw new ReadFileException("Не удалось прочитать файл регионов");
        }
        return new HashMap<>();
    }
}
