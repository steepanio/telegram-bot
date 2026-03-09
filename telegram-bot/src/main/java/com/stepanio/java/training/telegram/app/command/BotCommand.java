package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface BotCommand {
    boolean supports(IncomingMessageDto incomingMessageDto);
    CommandResponse handle(IncomingMessageDto incomingMessageDto);
}
