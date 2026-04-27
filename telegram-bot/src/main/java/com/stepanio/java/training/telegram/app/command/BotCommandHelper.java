package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.SearchFilter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

public class BotCommandHelper {


    public InlineKeyboardMarkup getInlineKeyboardMarkup(SearchFilter filter) {
        InlineKeyboardButton region = new InlineKeyboardButton("Регион"
                + (filter.getRegion() != null ? "[" + filter.getRegion() + "]" : ""));
        region.setCallbackData(BotCommandConstants.REGION_CALLBACK_DATA);
        InlineKeyboardButton experience = new InlineKeyboardButton("Минимальный опыт"
                + (filter.getExperience() != null ? "[" + filter.getExperience() + "]" : ""));
        experience.setCallbackData(BotCommandConstants.EXPERIENCE_CALLBACK_DATA);
        InlineKeyboardButton salary = new InlineKeyboardButton("Минимальная зарплата"
                + (filter.getSalaryMin() != null ? "[" + filter.getSalaryMin() + "]" : ""));
        salary.setCallbackData(BotCommandConstants.SALARY_CALLBACK_DATA);
        InlineKeyboardButton keyword = new InlineKeyboardButton("Слово для поиска"
                + (filter.getKeyword() != null ? "[" + filter.getKeyword() + "]" : ""));
        keyword.setCallbackData(BotCommandConstants.KEYWORD_CALLBACK_DATA);
        InlineKeyboardButton notification = new InlineKeyboardButton("Настройки нотификации"
                + (filter.getNotificationTime() != null ? "[" + filter.getNotificationTime() + "]" : ""));
        notification.setCallbackData(BotCommandConstants.NOTIFICATION_CALLBACK_DATA);
        InlineKeyboardButton ready = new InlineKeyboardButton("Готово");
        ready.setCallbackData(BotCommandConstants.READY_CALLBACK_DATA);

        return new InlineKeyboardMarkup(List.of(
                row(region),
                row(experience),
                row(salary),
                row(keyword),
                row(notification),
                row(ready)
        ));
    }

    private InlineKeyboardRow row(InlineKeyboardButton button) {
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(button);
        return row;
    }
}
