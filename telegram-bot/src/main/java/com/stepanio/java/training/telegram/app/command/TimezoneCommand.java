package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.repository.RepositoryService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.time.ZoneId;
import java.util.List;

public class TimezoneCommand implements BotCommand {
    private final RepositoryService repositoryService;

    public TimezoneCommand(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        if (incomingMessageDto.getText() != null) {
            return incomingMessageDto.getText().matches(BotCommandConstants.TIMEZONE_COMMAND);
        }
        return false;
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        BotUserDto user = repositoryService.getUser(incomingMessageDto.getUserId());
        String timezone = incomingMessageDto.getText();
        user.setTimezone(ZoneId.of(timezone));
        repositoryService.saveUser(user);
        InlineKeyboardButton menu = new InlineKeyboardButton("Menu");
        menu.setCallbackData(BotCommandConstants.MENU_CALLBACK_DATA);
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(menu);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(row));
        return new CommandResponse(incomingMessageDto.getChatId(), "Часовой пояс установлен: " + timezone, markup);
    }
}
