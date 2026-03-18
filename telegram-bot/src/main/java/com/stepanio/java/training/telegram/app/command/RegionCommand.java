package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.repository.JsonUserRepository;

public class RegionCommand implements BotCommand {
    private final JsonUserRepository repository;

    public RegionCommand(JsonUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return incomingMessageDto.getCommandName().equals(BotCommandConstants.REGION_COMMAND);
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        long chatId = incomingMessageDto.getChatId();
        BotUserDto botUserDto = repository.findById(incomingMessageDto.getUserId()).orElseThrow();
        String experience = incomingMessageDto.getText();
        botUserDto.getFilter().setExperience(Integer.valueOf(experience));
        return new CommandResponse(chatId, null);
    }
}
