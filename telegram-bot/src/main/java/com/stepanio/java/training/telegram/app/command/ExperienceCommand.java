package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;

public class ExperienceCommand implements BotCommand {

    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return incomingMessageDto.getCommandName().equals("/experience");
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        long chatId = incomingMessageDto.getChatId();
        String text = "";

        return new CommandResponse(chatId, text);
    }
}
