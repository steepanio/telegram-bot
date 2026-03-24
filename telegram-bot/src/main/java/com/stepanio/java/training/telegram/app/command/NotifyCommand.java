package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.dto.SearchFilter;
import com.stepanio.java.training.telegram.app.repository.JsonUserRepository;

import java.time.LocalTime;

public class NotifyCommand implements BotCommand {
    private final JsonUserRepository repository;

    public NotifyCommand(JsonUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return BotCommandConstants.NOTIFY_COMMAND.equals(incomingMessageDto.getCommandName());
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        BotUserDto user = repository.findById(incomingMessageDto.getUserId()).orElseThrow();
        user.getFilter().setNotificationTime(LocalTime.parse(incomingMessageDto.getText()));
        repository.save(user);
        return new CommandResponse(incomingMessageDto.getChatId(), null);
    }
}
