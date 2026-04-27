package com.stepanio.java.training.telegram.app.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.SearchFilter;
import com.stepanio.java.training.telegram.app.dto.VacancyDto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.time.ZoneId;
import java.util.List;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class JsonVacancyRepositoryTest {
    private static JsonVacancyRepository repository;
    private Path tempDir;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp () throws Exception {
        tempDir = Files.createTempDirectory("vacancies_test");
        repository = new JsonVacancyRepository(mapper.findAndRegisterModules(), tempDir.toFile());

        BotUserDto user = new BotUserDto(1234L, 4321L, true, new SearchFilter(), ZoneId.of("UTC"), 0, null);
        List<VacancyDto> vacancies = List.of(
                new VacancyDto("AHS", 2000, 4000, 2, "url"),
                new VacancyDto("ALT", 5000, 10000, 3, "anotherUrl"));
        repository.save(user, vacancies);
    }

    @AfterEach
    void tearDown () {
        for (File file : tempDir.toFile().listFiles()) {
            file.delete();
        }
        tempDir.toFile().delete();
    }

    @Test
    void existsTest() {
        boolean exists = exists(1234L);
        assertTrue(exists);
    }

    @Test
    void findByIdTest () {
        List<VacancyDto> byId = findById(1234L);
        assertFalse(byId.isEmpty());
        assertEquals(2, byId.size());
    }

    @Test
    void deleteTest () {
        deleteVacancies(1234L);
        List<VacancyDto> result = findById(1234L);
        assertTrue(result.isEmpty());
    }

    @Test
    void saveTest () {
        BotUserDto botUserDto = new BotUserDto(2345L, 5432L, true, new SearchFilter(), ZoneId.of("UTC"), 0, null);
        List<VacancyDto> vacancyDtoList = List.of(new VacancyDto("DDD", 4000, 10000,5,"url"));
        save(botUserDto, vacancyDtoList);
        List<VacancyDto> vacancies = findById(2345L);
        assertFalse(vacancies.isEmpty());
        assertEquals(1, vacancies.size());
        deleteVacancies(2345L);
    }

    static List<VacancyDto> findById(long id) {
        return repository.findById(id);
    }

    static void deleteVacancies (long id) {
        repository.delete(id);
    }

    static boolean exists (long id) {
        return repository.exists(id);
    }

    static void save(BotUserDto botUserDto, List<VacancyDto> list) {
        repository.save(botUserDto, list);
    }
}
