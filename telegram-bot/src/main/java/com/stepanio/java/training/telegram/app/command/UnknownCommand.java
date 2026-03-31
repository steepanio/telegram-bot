package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;

public class UnknownCommand implements BotCommand {

    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return true;
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        String text = "Неизвестная команда! Нажмите /menu для вывода списка команд";
        return new CommandResponse(incomingMessageDto.getChatId(), text, null);
    }
}
