package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.repository.RepositoryService;
import com.stepanio.java.training.telegram.app.scheduler.TaskScheduler;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

public class StopCommand implements BotCommand {
    private static final String YES = "yes";
    private static final String NO = "no";
    private final TaskScheduler taskScheduler;
    private final RepositoryService repositoryService;

    public StopCommand(TaskScheduler taskScheduler, RepositoryService repositoryService) {
        this.taskScheduler = taskScheduler;
        this.repositoryService = repositoryService;
    }

    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return BotCommandConstants.STOP_COMMAND.equals(incomingMessageDto.getCommandName())
                || incomingMessageDto.isCallback() && (YES.equals(incomingMessageDto.getCallbackData())
                || NO.equals(incomingMessageDto.getCallbackData()));
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        InlineKeyboardMarkup markup = getInlineKeyboardMarkup();
        if (YES.equals(incomingMessageDto.getCallbackData())) {
            taskScheduler.cancelTask(incomingMessageDto.getUserId());
            repositoryService.deleteUser(incomingMessageDto.getUserId());
            repositoryService.deleteVacancies(incomingMessageDto.getUserId());
            return new CommandResponse(incomingMessageDto.getChatId(), "Вся информация, связанная с вашими данными, удалена!\n" +
                    "Благодарим за использование Vacancy Tracker Bot", null);
        } else if (NO.equals(incomingMessageDto.getCallbackData())) {
            return new CommandResponse(incomingMessageDto.getChatId(),
                    "Благодарим, что продолжаете пользоваться Vacancy Tracker\nBot!", null);
        }
        return new CommandResponse(incomingMessageDto.getChatId(), "Вся информация, связанная с вашими данными, будет удалена!\n" +
                "Вы уверены", markup);
    }

    @NotNull
    private static InlineKeyboardMarkup getInlineKeyboardMarkup() {
        InlineKeyboardButton yes = new InlineKeyboardButton("Да");
        yes.setCallbackData(YES);
        InlineKeyboardButton no = new InlineKeyboardButton("Нет");
        no.setCallbackData(NO);
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(yes);
        row.add(no);
        return new InlineKeyboardMarkup(List.of(row));
    }
}
