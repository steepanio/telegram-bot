package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.repository.JsonUserRepository;
import com.stepanio.java.training.telegram.app.repository.JsonVacancyRepository;
import com.stepanio.java.training.telegram.app.scheduler.TaskScheduler;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

public class StopCommand implements BotCommand {
    private final JsonUserRepository userRepository;
    private final JsonVacancyRepository vacancyRepository;
    private final TaskScheduler taskScheduler;
    private static final String YES = "yes";
    private static final String NO = "no";

    public StopCommand(JsonUserRepository userRepository, JsonVacancyRepository vacancyRepository, TaskScheduler taskScheduler) {
        this.userRepository = userRepository;
        this.vacancyRepository = vacancyRepository;
        this.taskScheduler = taskScheduler;
    }

    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return incomingMessageDto.getCommandName().equals(BotCommandConstants.STOP_COMMAND)
                || YES.equals(incomingMessageDto.getCallbackData())
                || NO.equals(incomingMessageDto.getCallbackData());
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        InlineKeyboardButton yes = new InlineKeyboardButton("Да");
        yes.setCallbackData(YES);
        InlineKeyboardButton no = new InlineKeyboardButton("Нет");
        no.setCallbackData(NO);
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(yes);
        row.add(no);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(row));

        if (YES.equals(incomingMessageDto.getCallbackData())) {
            userRepository.delete(incomingMessageDto.getUserId());
            vacancyRepository.delete(incomingMessageDto.getUserId());
            taskScheduler.cancelTask(incomingMessageDto.getUserId());
            return new CommandResponse(incomingMessageDto.getChatId(), "Вся информация, связанная с вашими данными, удалена!\n" +
                    "Благодарим за использование Vacancy Tracker Bot", null);
        } else if (NO.equals(incomingMessageDto.getCallbackData())) {
            return new CommandResponse(incomingMessageDto.getChatId(),
                    "Благодарим, что продолжаете пользоваться Vacancy Tracker\nBot!", null);
        }
        return new CommandResponse(incomingMessageDto.getChatId(), "Вся информация, связанная с вашими данными, будет удалена!\n" +
                "Вы уверены", markup);
    }
}
