package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.dto.SearchFilter;
import com.stepanio.java.training.telegram.app.messageSender.MessageSenderImpl;
import com.stepanio.java.training.telegram.app.repository.JsonUserRepository;
import com.stepanio.java.training.telegram.app.repository.JsonVacancyRepository;
import com.stepanio.java.training.telegram.app.scheduler.TaskScheduler;
import com.stepanio.java.training.telegram.app.scheduler.task.VacancyLoadTask;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.stepan.trudvsem.client.http.TrudVsemRestClient;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReadyCommand implements BotCommand {
    private final JsonUserRepository jsonUserRepository;
    private final JsonVacancyRepository jsonVacancyRepository;
    private final TrudVsemRestClient trudVsemRestClient;
    private final MessageSenderImpl sender;
    private final TaskScheduler taskScheduler;

    public ReadyCommand(JsonUserRepository jsonUserRepository, JsonVacancyRepository jsonVacancyRepository, TrudVsemRestClient trudVsemRestClient, MessageSenderImpl sender, TaskScheduler taskScheduler) {
        this.jsonUserRepository = jsonUserRepository;
        this.jsonVacancyRepository = jsonVacancyRepository;
        this.trudVsemRestClient = trudVsemRestClient;
        this.sender = sender;
        this.taskScheduler = taskScheduler;
    }


    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return incomingMessageDto.getCommandName().equals(BotCommandConstants.READY_COMMAND)
                || BotCommandConstants.READY_CALLBACK_DATA.equals(incomingMessageDto.getCallbackData());
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        BotUserDto user = jsonUserRepository.findById(incomingMessageDto.getUserId()).orElseThrow();
        LocalTime notificationTime = user.getFilter().getNotificationTime();
        if (notificationTime == null) {
            return new CommandResponse(incomingMessageDto.getChatId(), "Установите время нотификации", null);
        }
        VacancyLoadTask task = new VacancyLoadTask(
                user.getUserId(),
                jsonUserRepository,
                jsonVacancyRepository,
                trudVsemRestClient,
                sender,
                incomingMessageDto
        );

        taskScheduler.scheduleOrReplace(notificationTime, task);
        SearchFilter filter = user.getFilter();
        String text = "Запланировано выполнение задачи \n нотификации о найденных вакансиях на " + filter.getNotificationTime() + ".\n"
                + "Будут искаться новые вакансии по \nследующим параметрам:\n\n" + getFilters(filter);
        InlineKeyboardButton button = new InlineKeyboardButton("Menu");
        button.setCallbackData(BotCommandConstants.MENU_CALLBACK_DATA);
        InlineKeyboardRow row = new InlineKeyboardRow(button);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(row));
        return new CommandResponse(incomingMessageDto.getChatId(), text, markup);
    }

    private String getFilters(SearchFilter filter) {
        String result = Stream.of(
                        filter.getKeyword() != null ? "Слово для поиска: " + filter.getKeyword() : null,
                        filter.getExperience() != null ? "Минимальный опыт: " + filter.getExperience() : null,
                        filter.getRegion() != null ? "Регион: " + filter.getRegion() : null,
                        filter.getSalaryMin() != null ? "Минимальная зарплата: " + filter.getSalaryMin() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n"));
        return result.isEmpty() ? "Фильтры не установлены" : result;
    }
}
