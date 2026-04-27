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

import java.util.List;

public class SalaryCommand implements BotCommand {
    private static final String SALARY_BUTTON = "salary_";
    private static final String SALARY_BUTTON_SKIP = SALARY_BUTTON + "skip";
    private final BotCommandHelper helper;
    private final RepositoryService repositoryService;


    public SalaryCommand(BotCommandHelper helper, RepositoryService repositoryService) {
        this.helper = helper;
        this.repositoryService = repositoryService;
    }

    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return BotCommandConstants.SALARY_COMMAND.equals(incomingMessageDto.getCommandName())
                || incomingMessageDto.isCallback() && (BotCommandConstants.SALARY_CALLBACK_DATA.equals(incomingMessageDto.getCallbackData())
                || incomingMessageDto.getCallbackData().startsWith(SALARY_BUTTON));
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
            if (arguments.size() != 1) {
                return new CommandResponse(incomingMessageDto.getChatId(),
                        "Неправильно введены данные. Вот пример для установки фильтра: " +
                                "/salary 25000", menuMarkup);
            }

            String salaryArgument = arguments.get(0);

            if (!salaryArgument.matches("\\d{1,20}")) {
                return new CommandResponse(incomingMessageDto.getChatId(),
                        "Неправильно введены данные. Вот пример для установки фильтра: " +
                                "/salary 25000", menuMarkup);
            } else {
                filter.setSalaryMin(Integer.valueOf(salaryArgument));
            }
            repositoryService.saveUser(user);
            return new CommandResponse(incomingMessageDto.getChatId(),
                    "Задайте критерии поиска и время нотификации.\n Затем нажмите \"Готово\", либо введите команду /ready\n",
                    helper.getInlineKeyboardMarkup(filter));
        } else if (incomingMessageDto.getCallbackData().equals(SALARY_BUTTON_SKIP)) {
            filter.setSalaryMin(null);
            repositoryService.saveUser(user);
            return new CommandResponse(incomingMessageDto.getChatId(),
                    "Задайте критерии поиска и время нотификации.\n Затем нажмите \"Готово\", либо введите команду /ready\n",
                    helper.getInlineKeyboardMarkup(filter));
        } else if (incomingMessageDto.getCallbackData().startsWith(SALARY_BUTTON)) {
            String callbackData = incomingMessageDto.getCallbackData();
            String salary = callbackData.replace(SALARY_BUTTON, "");
            filter.setSalaryMin(Integer.valueOf(salary));
            repositoryService.saveUser(user);
            return new CommandResponse(incomingMessageDto.getChatId(),
                    "Задайте критерии поиска и время нотификации.\n Затем нажмите \"Готово\", либо введите команду /ready\n",
                    helper.getInlineKeyboardMarkup(filter));
        }
        return new CommandResponse(incomingMessageDto.getChatId(), "Выбирайте желаемую зарплату из предложенных\n " +
                "вариантов или введите команду, например /salary 10000", markup);
    }

    @NotNull
    private InlineKeyboardMarkup getInlineKeyboardMarkup() {
        InlineKeyboardButton button1 = new InlineKeyboardButton("Не учитывать");
        button1.setCallbackData(SALARY_BUTTON_SKIP);
        InlineKeyboardButton button2 = new InlineKeyboardButton("25000 От 25 тыс. руб");
        button2.setCallbackData(SALARY_BUTTON + "25000");
        InlineKeyboardButton button3 = new InlineKeyboardButton("50000 От 50 тыс. руб");
        button3.setCallbackData(SALARY_BUTTON + "50000");
        InlineKeyboardButton button4 = new InlineKeyboardButton("100000 От 100 тыс. руб");
        button4.setCallbackData(SALARY_BUTTON + "100000");
        InlineKeyboardButton button5 = new InlineKeyboardButton("200000 От 200 тыс. руб");
        button5.setCallbackData(SALARY_BUTTON + "200000");
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
