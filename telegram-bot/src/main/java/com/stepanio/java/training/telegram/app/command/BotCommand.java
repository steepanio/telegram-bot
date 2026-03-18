package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;

public interface BotCommand {
    boolean supports(IncomingMessageDto incomingMessageDto);
    CommandResponse handle(IncomingMessageDto incomingMessageDto);
}
