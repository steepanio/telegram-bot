package com.stepanio.java.training.telegram.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.concurrent.ScheduledFuture;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandResponse {
    private Long chatId;
    private String text;
}
