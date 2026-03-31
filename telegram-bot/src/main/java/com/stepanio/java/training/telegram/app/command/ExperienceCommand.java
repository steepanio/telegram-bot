package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.repository.JsonUserRepository;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

public class ExperienceCommand implements BotCommand {
    private final JsonUserRepository repository;
    private static final String EXPERIENCE_BUTTON = "experience_";

    public ExperienceCommand(JsonUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return BotCommandConstants.EXPERIENCE_COMMAND.equals(incomingMessageDto.getCommandName())
                || BotCommandConstants.EXPERIENCE_CALLBACK_DATA.equals(incomingMessageDto.getCallbackData())
                || incomingMessageDto.getCallbackData().startsWith(EXPERIENCE_BUTTON);
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        BotUserDto user = repository.findById(incomingMessageDto.getUserId()).orElseThrow();
        InlineKeyboardButton button1 = new InlineKeyboardButton("Не учитывать");
        button1.setCallbackData(EXPERIENCE_BUTTON + "skip");
        InlineKeyboardButton button2 = new InlineKeyboardButton("0 Без опыта");
        button2.setCallbackData(EXPERIENCE_BUTTON + "0");
        InlineKeyboardButton button3 = new InlineKeyboardButton("1 От года");
        button3.setCallbackData(EXPERIENCE_BUTTON + "1");
        InlineKeyboardButton button4 = new InlineKeyboardButton("3 От трёх лет");
        button4.setCallbackData(EXPERIENCE_BUTTON + "3");
        InlineKeyboardButton button5 = new InlineKeyboardButton("5 От пяти лет");
        button5.setCallbackData(EXPERIENCE_BUTTON + "5");
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
                row(button1),
                row(button2),
                row(button3),
                row(button4),
                row(button5)
        ));
        if (!incomingMessageDto.isCallback()) {
            user.getFilter().setExperience(Integer.valueOf(incomingMessageDto.getArguments().get(0)));
            repository.save(user);
            return new CommandResponse(incomingMessageDto.getChatId(), null, null);
        } else if (incomingMessageDto.getCallbackData().equals(button1.getCallbackData())){
            user.getFilter().setExperience(null);
            repository.save(user);
            return new CommandResponse(incomingMessageDto.getChatId(), null, null);
        } else if (incomingMessageDto.getCallbackData().startsWith(EXPERIENCE_BUTTON)) {
            String callbackData = incomingMessageDto.getCallbackData();
            String experience = callbackData.replace(EXPERIENCE_BUTTON, "");
            user.getFilter().setExperience(Integer.valueOf(experience));
            repository.save(user);
            return new CommandResponse(incomingMessageDto.getChatId(), null, null);
        }

        return new CommandResponse(incomingMessageDto.getChatId(), "Выбирайте минимальный опыт", markup);
    }

    private InlineKeyboardRow row(InlineKeyboardButton button) {
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(button);
        return row;
    }
}
