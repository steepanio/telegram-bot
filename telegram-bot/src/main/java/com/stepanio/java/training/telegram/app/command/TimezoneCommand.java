package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.repository.JsonUserRepository;

import java.time.ZoneId;

public class TimezoneCommand implements BotCommand {
    private final JsonUserRepository repository;

    public TimezoneCommand(JsonUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return incomingMessageDto.getText().matches(BotCommandConstants.TIMEZONE_COMMAND);
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        BotUserDto user = repository.findById(incomingMessageDto.getUserId()).orElseThrow();
        String timezone = incomingMessageDto.getText();
        user.setTimezone(ZoneId.of(timezone));
        repository.save(user);
        return new CommandResponse(incomingMessageDto.getChatId(), "Часовой пояс установлен: " + timezone, null);
    }
}
