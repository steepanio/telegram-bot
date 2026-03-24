package com.stepanio.java.training.telegram.app.repository;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.VacancyDto;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonVacancyRepository implements VacancyRepository{
    private static final String FILE_NAME_PREFIX = "vacancies_";
    private static final String FILE_NAME_SUFFIX = ".json";
    private final File vacanciesDirectory = new File("vacancies");
    private final ObjectMapper mapper;

    public JsonVacancyRepository(ObjectMapper mapper, File usersDirectory) {
        this.mapper = mapper;
        if (!this.vacanciesDirectory.exists()) {
            boolean created = this.vacanciesDirectory.mkdirs();
            if (!created) {
                throw new RuntimeException("Папка с пользователями не создана");
            }
        }
    }

    @Override
    public void save(BotUserDto botUserDto, List<VacancyDto> vacancyDtoList) {
        File vacanciesFile = getVacanciesFile(botUserDto.getUserId());
        writeFile(vacancyDtoList, vacanciesFile);
    }

    @Override
    public List<VacancyDto> findById(Long userId) {
        File vacanciesFile = getVacanciesFile(userId);
        try {
            if (!vacanciesFile.exists()) {
                return new ArrayList<>();
            }
            return mapper.readValue(vacanciesFile, new TypeReference<List<VacancyDto>>() {
            });
        } catch (StreamReadException e) {
            throw new RuntimeException(e);
        } catch (DatabindException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(Long userId) {
        return getVacanciesFile(userId).exists();
    }

    private File getVacanciesFile(long userId) {
        return new File(vacanciesDirectory, buildVacanciesFileName(userId));
    }

    private VacancyDto readVacanciesFile(File vacanciesFile) {
        try {
            return mapper.readValue(vacanciesFile, VacancyDto.class);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать файл");
        }
    }

    private void writeFile(List<VacancyDto> vacancyDto, File vacanciesFile) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(vacanciesFile, vacancyDto);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать файл");
        }
    }

    private static String buildVacanciesFileName(long userId) {
        return FILE_NAME_PREFIX + userId + FILE_NAME_SUFFIX;
    }
}
