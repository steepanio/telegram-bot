package com.stepanio.java.training.telegram.app.repository;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CsvRegionsRepositoryTest {
    private final CsvRegionsRepository repository = new CsvRegionsRepository();

    @Test
    void readRowsTestWithValidFile () {
        Map<String, String> map = repository.readRows("/regions_valid.csv");

        assertEquals(2, map.size());
        assertEquals("Москва", map.get("01"));
        assertEquals("СПБ", map.get("02"));
    }

    @Test
    void readRowsTestWithDuplicates () {
        assertThrows(IllegalStateException.class,
                () -> repository.readRows("/regions_duplicate.csv"));
    }

    @Test
    void readRowsTestWithEmptyFile () {
        Map<String, String> map = repository.readRows("/regions_empty.csv");
        assertTrue(map.isEmpty());
    }
}
