package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.dto.SearchFilter;
import com.stepanio.java.training.telegram.app.repository.JsonUserRepository;

import java.util.Optional;

public class MenuCommand implements BotCommand {
    private final JsonUserRepository repository;

    public MenuCommand(JsonUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return incomingMessageDto.getCommandName().equals(BotCommandConstants.MENU_COMMAND);
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        long chatId = incomingMessageDto.getChatId();
        Optional<BotUserDto> user = repository.findById(incomingMessageDto.getUserId());
        SearchFilter filter = user.map(BotUserDto::getFilter).orElseThrow();
        String menu = "Задайте критерии поиска и время нотификации.\n Затем нажмите \"Готово\", либо введите команду /ready\n" +
                "Регион" + (filter.getRegion() != null ? "[" + filter.getRegion() + "]" : "") + "\n" +
                "Минимальный опыт" + (filter.getExperience() != null ? "[" + filter.getExperience() + "]" : "") + "\n" +
                "Минимальная зарплата" + (filter.getSalaryMin() !=null ? "[" + filter.getSalaryMin() + "]" : "") + "\n" +
                "Слово для поиска" + (filter.getKeyword() != null ? "[" + filter.getKeyword() + "]" : "") + "\n" +
                "Настройки нотификации" + (filter.getNotificationTime() != null ? "[" + filter.getNotificationTime() + "]" : "") + "\n" +
                "Готово";
        return new CommandResponse(chatId, menu);
    }
}
