package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.messageSender.MessageSenderImpl;
import com.stepanio.java.training.telegram.app.repository.JsonUserRepository;
import com.stepanio.java.training.telegram.app.repository.JsonVacancyRepository;
import com.stepanio.java.training.telegram.app.scheduler.TaskScheduler;
import com.stepanio.java.training.telegram.app.scheduler.task.VacancyLoadTask;
import ru.stepan.trudvsem.client.http.TrudVsemRestClient;

import java.time.LocalTime;

public class ReadyCommand implements BotCommand {
    private final JsonUserRepository jsonUserRepository;
    private final JsonVacancyRepository jsonVacancyRepository;
    private final TrudVsemRestClient trudVsemRestClient;
    private final MessageSenderImpl sender;

    public ReadyCommand(JsonUserRepository jsonUserRepository, JsonVacancyRepository jsonVacancyRepository, TrudVsemRestClient trudVsemRestClient, MessageSenderImpl sender) {
        this.jsonUserRepository = jsonUserRepository;
        this.jsonVacancyRepository = jsonVacancyRepository;
        this.trudVsemRestClient = trudVsemRestClient;
        this.sender = sender;
    }


    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return incomingMessageDto.getCommandName().equals(BotCommandConstants.READY_COMMAND);
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        TaskScheduler taskScheduler = new TaskScheduler();
        BotUserDto user = jsonUserRepository.findById(incomingMessageDto.getUserId()).orElseThrow();
        LocalTime notificationTime = user.getFilter().getNotificationTime();
        VacancyLoadTask vacancyLoadTask = new VacancyLoadTask(user.getUserId(), jsonUserRepository, jsonVacancyRepository, trudVsemRestClient, sender);
        taskScheduler.scheduleDailyTask(notificationTime, vacancyLoadTask);
        String text = "";
        return new CommandResponse(incomingMessageDto.getChatId(), text);
    }
}
