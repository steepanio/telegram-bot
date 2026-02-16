package com.stepanio.java.training.telegram.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BotResponse {
    private Long chatId;
    private String text;
    private ReplyKeyboard keyboard;
}
