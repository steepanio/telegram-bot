package com.stepanio.java.training.telegram.app.command.dispatcher;

import com.stepanio.java.training.telegram.app.command.BotCommand;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;

import java.util.List;

public class CommandDispatcher {
    private final List<BotCommand> commands;
    private final BotCommand defaultCommand;

    public CommandDispatcher(List<BotCommand> commands, BotCommand defaultCommand) {
        this.commands = commands;
        this.defaultCommand = defaultCommand;
    }

    public CommandResponse dispatch(IncomingMessageDto incomingMessageDto) {
        return commands.stream().filter(botCommand -> botCommand.supports(incomingMessageDto))
                .findFirst().map(botCommand -> botCommand.handle(incomingMessageDto)).orElse(defaultCommand.handle(incomingMessageDto));
    }
}
