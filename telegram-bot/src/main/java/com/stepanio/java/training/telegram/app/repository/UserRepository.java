package com.stepanio.java.training.telegram.app.repository;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;

import java.util.Optional;

public interface UserRepository {
    Optional<BotUserDto> findById(Long id);
    boolean exists(Long id);
    void save(BotUserDto botUserDto);
    void delete(Long id);
}
