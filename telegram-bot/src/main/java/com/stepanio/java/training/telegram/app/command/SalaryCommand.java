package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.repository.JsonUserRepository;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

public class SalaryCommand implements BotCommand {
    private final JsonUserRepository repository;
    private static final String SALARY_BUTTON = "salary_";

    public SalaryCommand(JsonUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return BotCommandConstants.SALARY_COMMAND.equals(incomingMessageDto.getCommandName())
                || BotCommandConstants.SALARY_CALLBACK_DATA.equals(incomingMessageDto.getCallbackData())
                || incomingMessageDto.getCallbackData().startsWith(SALARY_BUTTON);
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        BotUserDto user = repository.findById(incomingMessageDto.getUserId()).orElseThrow();

        InlineKeyboardButton button1 = new InlineKeyboardButton("Не учитывать");
        button1.setCallbackData(SALARY_BUTTON + "skip");
        InlineKeyboardButton button2 = new InlineKeyboardButton("25000 От 25 тыс. руб");
        button2.setCallbackData(SALARY_BUTTON + "25000");
        InlineKeyboardButton button3 = new InlineKeyboardButton("50000 От 50 тыс. руб");
        button3.setCallbackData(SALARY_BUTTON + "50000");
        InlineKeyboardButton button4 = new InlineKeyboardButton("100000 От 100 тыс. руб");
        button4.setCallbackData(SALARY_BUTTON + "100000");
        InlineKeyboardButton button5 = new InlineKeyboardButton("200000 От 200 тыс. руб");
        button5.setCallbackData(SALARY_BUTTON + "200000");
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
                row(button1),
                row(button2),
                row(button3),
                row(button4),
                row(button5)
        ));

        if (!incomingMessageDto.isCallback()) {
            user.getFilter().setSalaryMin(Integer.valueOf(incomingMessageDto.getArguments().get(0)));
            repository.save(user);
            return new CommandResponse(incomingMessageDto.getChatId(), null, null);
        } else if (incomingMessageDto.getCallbackData().equals(button1.getCallbackData())) {
            user.getFilter().setSalaryMin(null);
            repository.save(user);
            return new CommandResponse(incomingMessageDto.getChatId(), null, null);
        } else if (incomingMessageDto.getCallbackData().startsWith(SALARY_BUTTON)) {
            String callbackData = incomingMessageDto.getCallbackData();
            String salary = callbackData.replace(SALARY_BUTTON, "");
            user.getFilter().setSalaryMin(Integer.valueOf(salary));
            repository.save(user);
            return new CommandResponse(incomingMessageDto.getChatId(), null, null);
        }
        return new CommandResponse(incomingMessageDto.getChatId(), "Выбирайте желаемую зарплату из предложенных\n " +
                "вариантов или введите команду, например /salary 10000", markup);
    }

    private InlineKeyboardRow row(InlineKeyboardButton button) {
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(button);
        return row;
    }
}
