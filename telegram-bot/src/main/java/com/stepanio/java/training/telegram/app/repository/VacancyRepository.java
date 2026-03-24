package com.stepanio.java.training.telegram.app.repository;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.VacancyDto;

import java.util.List;

public interface VacancyRepository {
    void save(BotUserDto botUserDto, List<VacancyDto> vacancyDtoList);
    List<VacancyDto> findById(Long userId);
    boolean exists(Long userId);

}
