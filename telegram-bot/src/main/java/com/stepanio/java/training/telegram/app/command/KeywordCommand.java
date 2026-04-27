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

public class KeywordCommand implements BotCommand {
    private static final String KEYWORD_BUTTON = "keyword_";
    private final BotCommandHelper helper;
    private final RepositoryService repositoryService;

    public KeywordCommand(BotCommandHelper helper, RepositoryService repositoryService) {
        this.helper = helper;
        this.repositoryService = repositoryService;
    }

    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return BotCommandConstants.KEYWORD_COMMAND.equals(incomingMessageDto.getCommandName())
                || incomingMessageDto.isCallback() && (BotCommandConstants.KEYWORD_CALLBACK_DATA.equals(incomingMessageDto.getCallbackData())
                || incomingMessageDto.getCallbackData().startsWith(KEYWORD_BUTTON));
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

            if (arguments.size() < 1 || arguments.contains("")) {
                return new CommandResponse(incomingMessageDto.getChatId(),
                        "Неправильно введены данные. Вот пример для установки фильтра: " +
                                "/keyword Java", menuMarkup);
            }
            boolean allMatch = arguments.stream().anyMatch(arg -> arg.matches(".*\\d.*"));

            if (allMatch) {
                return new CommandResponse(incomingMessageDto.getChatId(),
                        "Неправильно введены данные. Вот пример для установки фильтра: " +
                                "/keyword Java", menuMarkup);
            } else {
                String keywordArgument = String.join(" ", arguments);
                filter.setKeyword(keywordArgument);
            }
            repositoryService.saveUser(user);
            return new CommandResponse(incomingMessageDto.getChatId(),
                    "Задайте критерии поиска и время нотификации.\n Затем нажмите \"Готово\", либо введите команду /ready\n"
                    , helper.getInlineKeyboardMarkup(filter));
        } else if (incomingMessageDto.getCallbackData().startsWith(KEYWORD_BUTTON)) {
            String callbackData = incomingMessageDto.getCallbackData();
            String keyword = callbackData.replace(KEYWORD_BUTTON, "");
            filter.setKeyword(keyword);
            repositoryService.saveUser(user);
            return new CommandResponse(incomingMessageDto.getChatId(),
                    "Задайте критерии поиска и время нотификации.\n Затем нажмите \"Готово\", либо введите команду /ready\n",
                    helper.getInlineKeyboardMarkup(filter));
        }
        return new CommandResponse(incomingMessageDto.getChatId(), "Выбирайте ключевое слово из предложенных вариантов или \n" +
                "введите команду, например /keyword программист kotlin", markup);
    }

    @NotNull
    private InlineKeyboardMarkup getInlineKeyboardMarkup() {
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

        return new InlineKeyboardMarkup(List.of(
                row(button1),
                row(button2),
                row(button3),
                row(button4),
                row(button5),
                row(button6)
        ));
    }

    private InlineKeyboardRow row(InlineKeyboardButton button) {
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(button);
        return row;
    }
}
