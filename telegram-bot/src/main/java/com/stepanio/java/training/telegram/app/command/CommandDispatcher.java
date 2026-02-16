package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.command.commands.BotCommand;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

public class CommandDispatcher {
    private final List<BotCommand> commands;
    private final BotCommand defaultCommand;

    public CommandDispatcher(List<BotCommand> commands, BotCommand defaultCommand) {
        this.commands = commands;
        this.defaultCommand = defaultCommand;
    }

    public SendMessage dispatch(IncomingMessageDto incomingMessageDto) {
        return commands.stream()
                .filter(c -> c.supports(incomingMessageDto))
                .findFirst()
                .map(c -> c.handle(incomingMessageDto))
                .orElse(defaultCommand.handle(incomingMessageDto));
    }
}
