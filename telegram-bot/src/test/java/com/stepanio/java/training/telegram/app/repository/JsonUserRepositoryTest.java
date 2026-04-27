package com.stepanio.java.training.telegram.app.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.SearchFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonUserRepositoryTest {
    private static JsonUserRepository repository;
    private Path tempDir;
    private ObjectMapper mapper = new ObjectMapper();


    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("users_test");
        repository = new JsonUserRepository(mapper.findAndRegisterModules(), tempDir.toFile());

        BotUserDto user = new BotUserDto(1234L, 4321L, true, new SearchFilter(), ZoneId.of("UTC+3"), 0, null);
        repository.save(user);
    }

    @AfterEach
    void tearDown() {
        for (File f : tempDir.toFile().listFiles()) {
            f.delete();
        }
        tempDir.toFile().delete();
    }

    @Test
    void existsTest() {
        boolean exists = exists(1234L);
        assertTrue(exists);
    }

    @Test
    void findByIdTest() {
        Optional<BotUserDto> user = findById(1234L);
        assertTrue(user.isPresent());
    }

    @Test
    void deleteTest() {
        deleteUser(1234L);
        Optional<BotUserDto> user = findById(1234L);
        assertFalse(user.isPresent());
    }

    @Test
    void saveTest() {
        BotUserDto botUserDto = new BotUserDto(2345L, 5432L, true, new SearchFilter(), ZoneId.of("UTC+3"), 0, null);
        save(botUserDto);
        Optional<BotUserDto> byId = findById(2345L);
        assertTrue(byId.isPresent());
        deleteUser(2345L);
    }

    static Optional<BotUserDto> findById(long id) {
        return repository.findById(id);
    }

    static void deleteUser(long id) {
        repository.delete(id);
    }

    static boolean exists(long id) {
        return repository.exists(id);
    }

    static void save(BotUserDto botUserDto) {
        repository.save(botUserDto);
    }
}
