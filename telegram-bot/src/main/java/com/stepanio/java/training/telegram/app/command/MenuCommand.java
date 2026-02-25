package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;

public class MenuCommand implements BotCommand {
    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return incomingMessageDto.getCommandName().equals(BotCommandConstants.MENU_COMMAND);
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        long chatId = incomingMessageDto.getChatId();
        return new CommandResponse(chatId, BotCommandConstants.MENU_COMMAND_RESPONSE);
    }
}
