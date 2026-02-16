package com.stepanio.java.training.telegram.app.command.commands;

import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class DefaultCommand implements BotCommand {
    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return true;
    }

    @Override
    public SendMessage handle(IncomingMessageDto incomingMessageDto) {
        return SendMessage.builder().chatId(String.valueOf(incomingMessageDto.getChatId()))
                .text("Неизвестная команда. Введите /start или /menu")
                .build();
    }
}
