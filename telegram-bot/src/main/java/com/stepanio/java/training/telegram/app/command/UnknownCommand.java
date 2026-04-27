package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

public class UnknownCommand implements BotCommand {

    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return true;
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        String text = "Неизвестная команда! Нажмите /menu для вывода списка команд";
        InlineKeyboardButton menu = new InlineKeyboardButton("Menu");
        menu.setCallbackData(BotCommandConstants.MENU_CALLBACK_DATA);
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(menu);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(row));
        return new CommandResponse(incomingMessageDto.getChatId(), text, markup);
    }
}
