package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.repository.JsonUserRepository;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.time.LocalTime;
import java.util.List;

public class NotifyCommand implements BotCommand {
    private final JsonUserRepository repository;
    private static final String NOTIFICATION_BUTTON = "notification_";

    public NotifyCommand(JsonUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return BotCommandConstants.NOTIFY_COMMAND.equals(incomingMessageDto.getCommandName())
                || BotCommandConstants.NOTIFICATION_CALLBACK_DATA.equals(incomingMessageDto.getCallbackData())
                || incomingMessageDto.getCallbackData().startsWith(NOTIFICATION_BUTTON);
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        BotUserDto user = repository.findById(incomingMessageDto.getUserId()).orElseThrow();
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

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
                row(button1),
                row(button2),
                row(button3),
                row(button4),
                row(button5)
        ));

        if (!incomingMessageDto.isCallback()) {
            List<String> arguments = incomingMessageDto.getArguments();
            int hours = Integer.parseInt(arguments.get(0));
            int minutes = Integer.parseInt(arguments.get(1));
            LocalTime localTime = LocalTime.of(hours, minutes);
            user.getFilter().setNotificationTime(localTime);
            repository.save(user);
            return new CommandResponse(incomingMessageDto.getChatId(), null, null);
        } else if (incomingMessageDto.getCallbackData().startsWith(NOTIFICATION_BUTTON)) {
            String callbackData = incomingMessageDto.getCallbackData();
            String notification = callbackData.replace(NOTIFICATION_BUTTON, "");
            String[] split = notification.split(":");
            int hours = Integer.parseInt(split[0]);
            int minutes = Integer.parseInt(split[1]);
            LocalTime notificationTime = LocalTime.of(hours, minutes);
            user.getFilter().setNotificationTime(notificationTime);
            repository.save(user);
            return new CommandResponse(incomingMessageDto.getChatId(), null, null);
        }
        return new CommandResponse(incomingMessageDto.getChatId(), "Выбирайте время нотификации, либо введите команду,\n" +
                "например /notify 15 30(разделитель часов и минут - пробел)", markup);
    }

    private InlineKeyboardRow row(InlineKeyboardButton button) {
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(button);
        return row;
    }
}
