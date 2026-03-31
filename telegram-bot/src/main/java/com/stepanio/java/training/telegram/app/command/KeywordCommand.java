package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.repository.JsonUserRepository;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

public class KeywordCommand implements BotCommand {
    private final JsonUserRepository repository;
    private static final String KEYWORD_BUTTON = "keyword_";

    public KeywordCommand(JsonUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return BotCommandConstants.KEYWORD_COMMAND.equals(incomingMessageDto.getCommandName())
                || BotCommandConstants.KEYWORD_CALLBACK_DATA.equals(incomingMessageDto.getCallbackData())
                || incomingMessageDto.getCallbackData().startsWith(KEYWORD_BUTTON);
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        BotUserDto user = repository.findById(incomingMessageDto.getUserId()).orElseThrow();
        InlineKeyboardButton button1 = new InlineKeyboardButton("Java");
        button1.setCallbackData(KEYWORD_BUTTON + "java");
        InlineKeyboardButton button2 = new InlineKeyboardButton("Инженер-программист");
        button2.setCallbackData(KEYWORD_BUTTON + "Инженер-программист");
        InlineKeyboardButton button3 = new InlineKeyboardButton("Стажировка Java");
        button3.setCallbackData(KEYWORD_BUTTON + "Стажировка Java");
        InlineKeyboardButton button4 = new InlineKeyboardButton("Программист-стажер");
        button4.setCallbackData(KEYWORD_BUTTON + "Программист");
        InlineKeyboardButton button5 = new InlineKeyboardButton("Developer");
        button5.setCallbackData(KEYWORD_BUTTON + "Developer");
        InlineKeyboardButton button6 = new InlineKeyboardButton("Software engineer");
        button6.setCallbackData(KEYWORD_BUTTON + "Software engineer");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
                row(button1),
                row(button2),
                row(button3),
                row(button4),
                row(button5),
                row(button6)
        ));

        if (!incomingMessageDto.isCallback()) {
            user.getFilter().setKeyword(incomingMessageDto.getArguments().get(0));
            repository.save(user);
            return new CommandResponse(incomingMessageDto.getChatId(), null, null);
        } else if (incomingMessageDto.getCallbackData().startsWith(KEYWORD_BUTTON)) {
            String callbackData = incomingMessageDto.getCallbackData();
            String keyword = callbackData.replace(KEYWORD_BUTTON, "");
            user.getFilter().setKeyword(keyword);
            repository.save(user);
            return new CommandResponse(incomingMessageDto.getChatId(), null, null);
        }
        return new CommandResponse(incomingMessageDto.getChatId(), "Выбирайте ключевое слово из предложенных вариантов или \n" +
                "введите команду, например /keyword программист kotlin", markup);
    }

    private InlineKeyboardRow row(InlineKeyboardButton button) {
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(button);
        return row;
    }
}
