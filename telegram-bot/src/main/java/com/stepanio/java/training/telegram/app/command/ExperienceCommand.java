package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.dto.SearchFilter;
import com.stepanio.java.training.telegram.app.repository.JsonUserRepository;

import java.util.List;

public class ExperienceCommand implements BotCommand {
    private final JsonUserRepository repository;

    public ExperienceCommand(JsonUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return BotCommandConstants.EXPERIENCE_COMMAND.equals(incomingMessageDto.getCommandName());
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        BotUserDto user = repository.findById(incomingMessageDto.getUserId()).orElseThrow();
        user.getFilter().setExperience(Integer.valueOf(incomingMessageDto.getText()));
        repository.save(user);
        return new CommandResponse(incomingMessageDto.getChatId(), null);
    }
}
