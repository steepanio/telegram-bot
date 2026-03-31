package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.dto.SearchFilter;
import com.stepanio.java.training.telegram.app.repository.JsonUserRepository;

import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StartCommand implements BotCommand {
    private final JsonUserRepository repository;

    public StartCommand(JsonUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return BotCommandConstants.START_COMMAND.equals(incomingMessageDto.getCommandName());
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        String response;
        Optional<BotUserDto> user = repository.findById(incomingMessageDto.getUserId());
        if (user.isEmpty()) {
            repository.save(new BotUserDto(incomingMessageDto.getUserId(), incomingMessageDto.getChatId(),
                    true, null, ZoneId.of("UTC"), 0, null));
            response = "Вы успешно зарегистрировались в Vacancy Tracker Bot!\n" +
                    "Будет использован часовой пояс UTC, для изменения введите\n" +
                    "часовой пояс в формате UTC+3\n" +
                    "Используйте команду /menu для получения списка команд";
        } else {
            SearchFilter filter = user.map(BotUserDto::getFilter).orElseThrow();
            response = "Vacancy Tracker Bot приветствует вас!\n" +
                    "Вы ранее регистрировались в нашем боте, можете приступать к\nего использованию.\n" +
                    "Сейчас используется часовой пояс UTC, для изменения введите,\nнапример UTC+3:30\n" +
                    "\n" + "Ранее заданные фильтры:\n" + getFilters(filter) + "\n" + "\n" +
                    "Введите команду /menu для получения списка команд";
        }
        return new CommandResponse(incomingMessageDto.getChatId(), response, null);
    }

    public String getFilters(SearchFilter filter) {
        String result = Stream.of(
                        filter.getKeyword() != null ? "Слово для поиска: " + filter.getKeyword() : null,
                        filter.getExperience() != null ? "Минимальный опыт: " + filter.getExperience() : null,
                        filter.getRegion() != null ? "Регион: " + filter.getRegion() : null,
                        filter.getSalaryMin() != null ? "Минимальная зарплата: " + filter.getSalaryMin() : null,
                        filter.getNotificationTime() != null ? "Настройки нотификации: " + filter.getNotificationTime() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n"));
        return result.isEmpty() ? "Фильтры не установлены" : result;
    }
}
