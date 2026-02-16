package com.stepanio.java.training.telegram.app.command.commands;

import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class KeywordCommand implements BotCommand {
    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return incomingMessageDto.getCommandName().equals("/keyword");
    }

    @Override
    public SendMessage handle(IncomingMessageDto incomingMessageDto) {
        return null;
    }
}
