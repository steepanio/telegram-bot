package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.dto.SearchFilter;
import com.stepanio.java.training.telegram.app.messageSender.MessageSenderImpl;
import com.stepanio.java.training.telegram.app.repository.RepositoryService;
import com.stepanio.java.training.telegram.app.scheduler.TaskScheduler;
import com.stepanio.java.training.telegram.app.scheduler.task.VacancyLoadTask;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.stepan.trudvsem.client.http.TrudVsemRestClient;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReadyCommand implements BotCommand {

    private final TrudVsemRestClient trudVsemRestClient;
    private final MessageSenderImpl sender;
    private final TaskScheduler taskScheduler;
    private final RepositoryService repositoryService;

    public ReadyCommand(TrudVsemRestClient trudVsemRestClient, MessageSenderImpl sender, TaskScheduler taskScheduler, RepositoryService repositoryService) {
        this.trudVsemRestClient = trudVsemRestClient;
        this.sender = sender;
        this.taskScheduler = taskScheduler;
        this.repositoryService = repositoryService;
    }


    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return BotCommandConstants.READY_COMMAND.equals(incomingMessageDto.getCommandName())
                || incomingMessageDto.isCallback() && BotCommandConstants.READY_CALLBACK_DATA.equals(incomingMessageDto.getCallbackData());
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        BotUserDto user = repositoryService.getUser(incomingMessageDto.getUserId());
        SearchFilter filter = user.getFilter();
        if (filter == null) {
            user.setFilter(new SearchFilter());
            filter = user.getFilter();
        }
        LocalTime notificationTime = filter.getNotificationTime();
        ZoneId zoneId = user.getTimezone();
        InlineKeyboardButton menu = new InlineKeyboardButton("Menu");
        menu.setCallbackData(BotCommandConstants.MENU_CALLBACK_DATA);
        InlineKeyboardRow menuRow = new InlineKeyboardRow();
        menuRow.add(menu);
        InlineKeyboardMarkup menuMarkup = new InlineKeyboardMarkup(List.of(menuRow));
        if (notificationTime == null) {
            return new CommandResponse(incomingMessageDto.getChatId(), "Установите время нотификации", menuMarkup);
        }
            VacancyLoadTask task = new VacancyLoadTask(
                    user.getUserId(),
                    trudVsemRestClient,
                    sender, incomingMessageDto,
                    repositoryService
            );
            taskScheduler.scheduleOrReplace(notificationTime, zoneId , task);

        String text = "Запланировано выполнение задачи \n нотификации о найденных вакансиях на " + filter.getNotificationTime() + ".\n"
                + "Будут искаться новые вакансии по \nследующим параметрам:\n\n" + getFilters(filter);
        return new CommandResponse(incomingMessageDto.getChatId(), text, menuMarkup);
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
