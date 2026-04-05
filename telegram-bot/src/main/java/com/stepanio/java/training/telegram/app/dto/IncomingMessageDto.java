package com.stepanio.java.training.telegram.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomingMessageDto {
    private long userId;
    private long chatId;
    private Integer messageId;
    private String text;
    private String commandName;
    private List<String> arguments;
    private boolean isCallback;
    private String callbackData;
}
