package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.dto.SearchFilter;
import com.stepanio.java.training.telegram.app.repository.RepositoryService;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.time.LocalTime;
import java.util.List;

public class NotifyCommand implements BotCommand {
    private static final String NOTIFICATION_BUTTON = "notification_";
    private final BotCommandHelper helper;
    private final RepositoryService repositoryService;

    public NotifyCommand(BotCommandHelper helper, RepositoryService repositoryService) {
        this.helper = helper;
        this.repositoryService = repositoryService;
    }

    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return BotCommandConstants.NOTIFY_COMMAND.equals(incomingMessageDto.getCommandName())
                || incomingMessageDto.isCallback() && (BotCommandConstants.NOTIFICATION_CALLBACK_DATA.equals(incomingMessageDto.getCallbackData())
                || incomingMessageDto.getCallbackData().startsWith(NOTIFICATION_BUTTON));
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        BotUserDto user = repositoryService.getUser(incomingMessageDto.getUserId());
        SearchFilter filter = user.getFilter();
        if (filter == null) {
            user.setFilter(new SearchFilter());
            filter = user.getFilter();
        }
        InlineKeyboardMarkup markup = getInlineKeyboardMarkup();
        InlineKeyboardButton menu = new InlineKeyboardButton("Menu");
        menu.setCallbackData(BotCommandConstants.MENU_CALLBACK_DATA);
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(menu);
        InlineKeyboardMarkup menuMarkup = new InlineKeyboardMarkup(List.of(row));

        if (!incomingMessageDto.isCallback()) {
            List<String> arguments = incomingMessageDto.getArguments();

            if (arguments.size() != 2) {
                return new CommandResponse(incomingMessageDto.getChatId(),
                        "Неправильно введены данные. Вот пример для установки фильтра: " +
                                "/notify 7 15 (разделитель пробел)", menuMarkup);
            }

            int hours = Integer.parseInt(arguments.get(0));
            int minutes = Integer.parseInt(arguments.get(1));
            LocalTime localTime;

            if (hours > 23 || hours < 0 || minutes > 59 || minutes < 0) {
                return new CommandResponse(incomingMessageDto.getChatId(),
                        "Неправильно введены данные. Вот пример для установки фильтра: " +
                                "/notify 7 15 (разделитель пробел)", menuMarkup);
            } else {
                localTime = LocalTime.of(hours, minutes);
            }
            filter.setNotificationTime(localTime);
            repositoryService.saveUser(user);
            return new CommandResponse(incomingMessageDto.getChatId(),
                    "Задайте критерии поиска и время нотификации.\n Затем нажмите \"Готово\", либо введите команду /ready\n",
                    helper.getInlineKeyboardMarkup(filter));
        } else if (incomingMessageDto.getCallbackData().startsWith(NOTIFICATION_BUTTON)) {
            String callbackData = incomingMessageDto.getCallbackData();
            String notification = callbackData.replace(NOTIFICATION_BUTTON, "");
            String[] split = notification.split(":");
            int hours = Integer.parseInt(split[0]);
            int minutes = Integer.parseInt(split[1]);
            LocalTime notificationTime = LocalTime.of(hours, minutes);
            filter.setNotificationTime(notificationTime);
            repositoryService.saveUser(user);
            return new CommandResponse(incomingMessageDto.getChatId(),
                    "Задайте критерии поиска и время нотификации.\n Затем нажмите \"Готово\", либо введите команду /ready\n",
                    helper.getInlineKeyboardMarkup(filter));
        }
        return new CommandResponse(incomingMessageDto.getChatId(), "Выбирайте время нотификации, либо введите команду,\n" +
                "например /notify 15 30(разделитель часов и минут - пробел)", markup);
    }

    @NotNull
    private InlineKeyboardMarkup getInlineKeyboardMarkup() {
        InlineKeyboardButton button1 = new InlineKeyboardButton("12:00");
        button1.setCallbackData(NOTIFICATION_BUTTON + "12:00");
        InlineKeyboardButton button2 = new InlineKeyboardButton("15:00");
        button2.setCallbackData(NOTIFICATION_BUTTON + "15:00");
        InlineKeyboardButton button3 = new InlineKeyboardButton("17:00");
        button3.setCallbackData(NOTIFICATION_BUTTON + "17:00");
        InlineKeyboardButton button4 = new InlineKeyboardButton("19:00");
        button4.setCallbackData(NOTIFICATION_BUTTON + "19:00");
        InlineKeyboardButton button5 = new InlineKeyboardButton("21:00");
        button5.setCallbackData(NOTIFICATION_BUTTON + "21:00");

        return new InlineKeyboardMarkup(List.of(
                row(button1),
                row(button2),
                row(button3),
                row(button4),
                row(button5)
        ));
    }

    private InlineKeyboardRow row(InlineKeyboardButton button) {
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(button);
        return row;
    }
}
