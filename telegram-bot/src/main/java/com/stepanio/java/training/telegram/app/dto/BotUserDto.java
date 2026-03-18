package com.stepanio.java.training.telegram.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BotUserDto {
    private long userId;
    private long chatId;
    private boolean active;
    private SearchFilter filter;
    private ZoneId timezone;
}
