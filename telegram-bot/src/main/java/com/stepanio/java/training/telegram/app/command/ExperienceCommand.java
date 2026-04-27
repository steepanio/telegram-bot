package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.dto.SearchFilter;
import com.stepanio.java.training.telegram.app.repository.RepositoryService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

public class ExperienceCommand implements BotCommand {
    private static final String EXPERIENCE_BUTTON = "experience_";
    private static final String EXPERIENCE_BUTTON_SKIP = EXPERIENCE_BUTTON + "skip";
    private final BotCommandHelper helper;
    private final RepositoryService repositoryService;

    public ExperienceCommand(BotCommandHelper helper, RepositoryService repositoryService) {
        this.helper = helper;
        this.repositoryService = repositoryService;
    }

    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return BotCommandConstants.EXPERIENCE_COMMAND.equals(incomingMessageDto.getCommandName())
                || incomingMessageDto.isCallback()
                && (BotCommandConstants.EXPERIENCE_CALLBACK_DATA.equals(incomingMessageDto.getCallbackData())
                || incomingMessageDto.getCallbackData().startsWith(EXPERIENCE_BUTTON));
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        BotUserDto user = repositoryService.getUser(incomingMessageDto.getUserId());
        SearchFilter filter = user.getFilter();
        if (filter == null) {
            user.setFilter(new SearchFilter());
            filter = user.getFilter();
        }
        InlineKeyboardMarkup markup = createMarkup();
        InlineKeyboardButton menu = new InlineKeyboardButton("Menu");
        menu.setCallbackData(BotCommandConstants.MENU_CALLBACK_DATA);
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(menu);
        InlineKeyboardMarkup menuMarkup = new InlineKeyboardMarkup(List.of(row));
        List<String> arguments = incomingMessageDto.getArguments();

        if (!incomingMessageDto.isCallback()) {
            if (arguments.size() != 1) {
                return new CommandResponse(incomingMessageDto.getChatId(),
                        "Неправильно введены данные. Вот пример для установки фильтра:" +
                        "/experience 5", menuMarkup);
            }
            String experienceFromArguments = arguments.get(0);

            if (!experienceFromArguments.matches("\\d{1,7}")) {
                return new CommandResponse(incomingMessageDto.getChatId(),
                        "Неправильно введены данные. Вот пример для установки фильтра:" +
                        "/experience 5", menuMarkup);
            } else {
                filter.setExperience(Integer.valueOf(experienceFromArguments));
            }
                repositoryService.saveUser(user);
            return new CommandResponse(incomingMessageDto.getChatId(),
                    "Задайте критерии поиска и время нотификации.\n Затем нажмите \"Готово\", либо введите команду /ready\n"
                    , helper.getInlineKeyboardMarkup(filter));
        } else if (incomingMessageDto.getCallbackData().equals(EXPERIENCE_BUTTON_SKIP)) {
            filter.setExperience(null);
                repositoryService.saveUser(user);
            return new CommandResponse(incomingMessageDto.getChatId(),
                    "Задайте критерии поиска и время нотификации.\n Затем нажмите \"Готово\", либо введите команду /ready\n",
                    helper.getInlineKeyboardMarkup(filter));
        } else if (incomingMessageDto.getCallbackData().startsWith(EXPERIENCE_BUTTON)) {
            String callbackData = incomingMessageDto.getCallbackData();
            String experience = callbackData.replace(EXPERIENCE_BUTTON, "");
            filter.setExperience(Integer.valueOf(experience));
            repositoryService.saveUser(user);
            return new CommandResponse(incomingMessageDto.getChatId(),
                    "Задайте критерии поиска и время нотификации.\n Затем нажмите \"Готово\", либо введите команду /ready\n",
                    helper.getInlineKeyboardMarkup(filter));
        }
        return new CommandResponse(incomingMessageDto.getChatId(), "Выбирайте минимальный опыт", markup);
    }

    private InlineKeyboardRow row(InlineKeyboardButton button) {
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(button);
        return row;
    }

    private InlineKeyboardMarkup createMarkup() {
        InlineKeyboardButton button1 = new InlineKeyboardButton("Не учитывать");
        button1.setCallbackData(EXPERIENCE_BUTTON_SKIP);
        InlineKeyboardButton button2 = new InlineKeyboardButton("0 Без опыта");
        button2.setCallbackData(EXPERIENCE_BUTTON + "0");
        InlineKeyboardButton button3 = new InlineKeyboardButton("1 От года");
        button3.setCallbackData(EXPERIENCE_BUTTON + "1");
        InlineKeyboardButton button4 = new InlineKeyboardButton("3 От трёх лет");
        button4.setCallbackData(EXPERIENCE_BUTTON + "3");
        InlineKeyboardButton button5 = new InlineKeyboardButton("5 От пяти лет");
        button5.setCallbackData(EXPERIENCE_BUTTON + "5");
        return new InlineKeyboardMarkup(List.of(
                row(button1),
                row(button2),
                row(button3),
                row(button4),
                row(button5)
        ));
    }
}
