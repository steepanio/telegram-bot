package com.stepanio.java.training.telegram.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BotUserDto {
    private long userId;
    private long chatId;
    private boolean active;

}
