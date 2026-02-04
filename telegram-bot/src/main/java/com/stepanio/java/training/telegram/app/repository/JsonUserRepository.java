package com.stepanio.java.training.telegram.app.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
public class JsonUserRepository implements UserRepository {
    private File dir = new File("users");
    private ObjectMapper mapper;

    public JsonUserRepository(ObjectMapper mapper) {
        this.mapper = mapper;
        if (!this.dir.exists()) {
            this.dir.mkdirs();
        }
    }

    @Override
    public Optional<BotUserDto> findById(Long id) {
        if (!exists(id)) {
            return Optional.empty();
        }
        File[] files = dir.listFiles();
        Optional<File> first = Arrays.stream(files).filter(file -> file.getName().contains("user_" + id + ".json")).findFirst();
        if (first.isEmpty()) {
            return Optional.empty();
        }
        File file = first.get();
        try {
            BotUserDto botUserDto = mapper.readValue(file, BotUserDto.class);
            return Optional.of(botUserDto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(Long id) {
        String fileName = "user_" + id + ".json";
        File fileUser = new File(dir, fileName);
        return fileUser.exists();
    }

    @Override
    public void save(BotUserDto botUserDto) {
        File file = new File(dir, "user_" + botUserDto.getUserId() + ".json");
        if (!file.exists()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", botUserDto.getUserId());
            jsonObject.put("chatId", botUserDto.getChatId());
            jsonObject.put("active", botUserDto.isActive());

            try {
                FileWriter writer = new FileWriter(file);
                String string = jsonObject.toString();
                writer.write(string);
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                String stringJsonObject = Files.readString(file.toPath());
                JSONObject jsonObject = new JSONObject(stringJsonObject);
                jsonObject.put("chatId", botUserDto.getChatId());
                jsonObject.put("active", botUserDto.isActive());

                FileWriter writer = new FileWriter(file);
                String string = jsonObject.toString();
                writer.write(string);
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void delete(Long id) {
        File[] files = dir.listFiles();
        Optional<File> first = Arrays.stream(files).filter(file -> file.getName().contains("user_" + id + ".json")).findFirst();
        if (first.isPresent()) {
            File file = first.get();
            boolean delete = file.delete();

            if (!delete) {
                log.error("Не удалось удалить файл");
            }
        } else {
            log.error("Не удалось найти файл");
        }

    }
}

