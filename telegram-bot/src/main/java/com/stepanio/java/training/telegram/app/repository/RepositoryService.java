package com.stepanio.java.training.telegram.app.repository;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.VacancyDto;
import com.stepanio.java.training.telegram.app.exception.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RepositoryService {
    private final JsonUserRepository userRepository;
    private final JsonVacancyRepository vacancyRepository;

    public RepositoryService(JsonUserRepository userRepository, JsonVacancyRepository vacancyRepository) {
        this.userRepository = userRepository;
        this.vacancyRepository = vacancyRepository;
    }

    public BotUserDto getUser(long userId) {
        try {
            return userRepository.findById(userId).orElse(null);
        } catch (UserNotFoundException e) {
            log.warn("WARNING: " + e.getMessage());
            return new BotUserDto();
        }
    }

    public List<VacancyDto> getVacancies(long userId) {
        try {
            return vacancyRepository.findById(userId);
        } catch (VacanciesNotFoundException e) {
            log.warn("WARNING: " + e.getMessage());
            return new ArrayList<>();
        }

    }

    public void saveUser(BotUserDto botUserDto) {
        try {
            userRepository.save(botUserDto);
        } catch (WriteFileException e) {
            log.warn("WARNING: " + e.getMessage());
        }
    }

    public void saveVacancies (BotUserDto botUserDto, List<VacancyDto> vacancies) {
        try {
            vacancyRepository.save(botUserDto, vacancies);
        } catch (WriteFileException e) {
            log.warn("WARNING: " + e.getMessage());
        }
    }

    public void deleteUser (Long userId) {
        try {
            userRepository.delete(userId);
        } catch (ReadFileException e) {
            log.warn("WARNING: " + e.getMessage());
        } catch (DeleteFileException e) {
            log.warn("WARNING: " + e.getMessage());
        }
    }

    public void deleteVacancies (Long userId) {
        try {
            vacancyRepository.delete(userId);
        } catch (ReadFileException | DeleteFileException e) {
            log.warn("WARNING: " + e.getMessage());
        }
    }
}
