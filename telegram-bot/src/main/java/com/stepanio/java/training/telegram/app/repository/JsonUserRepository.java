package com.stepanio.java.training.telegram.app.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Slf4j
public class JsonUserRepository implements UserRepository {
    private static final String FILE_NAME_PREFIX = "user_";
    private static final String FILE_NAME_SUFFIX = ".json";
    private final File usersDirectory = new File("users");
    private final ObjectMapper mapper;

    public JsonUserRepository(ObjectMapper mapper) {
        this.mapper = mapper;
        if (!this.usersDirectory.exists()) {
            throw new RuntimeException("Папка с пользователями не создана");
        }
        this.usersDirectory.mkdirs();
    }

    @Override
    public Optional<BotUserDto> findById(Long id) {
        return Optional.of(id)
                .filter(this::exists)
                .map(this::getUserFile)
                .map(this::readUserFile);
    }

    @Override
    public boolean exists(Long id) {
        return getUserFile(id).exists();
    }

    @Override
    public void save(BotUserDto botUserDto) {
        File userFile = getUserFile(botUserDto.getUserId());
        writeFile(botUserDto, userFile);
    }

    @Override
    public void delete(Long id) {
        File userFile = getUserFile(id);
        if (userFile.exists()) {
            boolean delete = userFile.delete();
            if (!delete) {
                log.error("Не удалось удалить файл");
            }
        } else {
            log.error("Не удалось найти файл");
        }
    }

    private File getUserFile(long userId) {
        return new File(usersDirectory, buildUserFileName(userId));
    }

    private BotUserDto readUserFile(File userFile) {
        try {
            return mapper.readValue(userFile, BotUserDto.class);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать файл пользователя");
        }
    }

    private void writeFile(BotUserDto userDto, File userFile) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(userFile, userDto);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать файл пользователя");
        }
    }

    private static String buildUserFileName(long userId) {
        return FILE_NAME_PREFIX + userId + FILE_NAME_SUFFIX;
    }
}

