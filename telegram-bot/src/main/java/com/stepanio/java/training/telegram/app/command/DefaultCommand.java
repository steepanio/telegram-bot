package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class DefaultCommand implements BotCommand {

    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return true;
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        long chatId = incomingMessageDto.getChatId();
        String text = "";

        return new CommandResponse(chatId, text);
    }
}
