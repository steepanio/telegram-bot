package com.stepanio.java.training.telegram.app.repository;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;

import java.util.Optional;

public interface UserRepository {
    Optional<BotUserDto> findById(Long userId);
    boolean exists(Long userId);
    void save(BotUserDto botUserDto);
    void delete(Long userId);
}
