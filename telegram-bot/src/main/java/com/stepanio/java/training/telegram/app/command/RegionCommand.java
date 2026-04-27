package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.dto.SearchFilter;
import com.stepanio.java.training.telegram.app.repository.CsvRegionsRepository;
import com.stepanio.java.training.telegram.app.repository.RepositoryService;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegionCommand implements BotCommand {
    private static final String REGION_BUTTON = "region_";
    private static final String REGION_PAGE = "page_region";
    private final Map<String, String> regions;
    private final BotCommandHelper helper;
    private final RepositoryService repositoryService;

    public RegionCommand(CsvRegionsRepository regionsRepository, BotCommandHelper helper, RepositoryService repositoryService) {
        this.regions = regionsRepository.readRows("/russian_regions_codes.csv");
        this.helper = helper;
        this.repositoryService = repositoryService;
    }

    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return BotCommandConstants.REGION_COMMAND.equals(incomingMessageDto.getCommandName())
                || incomingMessageDto.isCallback()
                && (BotCommandConstants.REGION_CALLBACK_DATA.equals(incomingMessageDto.getCallbackData())
                || incomingMessageDto.getCallbackData().startsWith(REGION_BUTTON)
                || incomingMessageDto.getCallbackData().startsWith(REGION_PAGE));
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        BotUserDto user = repositoryService.getUser(incomingMessageDto.getUserId());
        SearchFilter filter = user.getFilter();
        if (filter == null) {
            user.setFilter(new SearchFilter());
            filter = user.getFilter();
        }

        InlineKeyboardButton menu = new InlineKeyboardButton("Menu");
        menu.setCallbackData(BotCommandConstants.MENU_CALLBACK_DATA);
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(menu);
        InlineKeyboardMarkup menuMarkup = new InlineKeyboardMarkup(List.of(row));
        List<String> arguments = incomingMessageDto.getArguments();

        if (!incomingMessageDto.isCallback()) {
            if (arguments.size() != 1) {
                return new CommandResponse(incomingMessageDto.getChatId(),
                        "Неправильно введены данные. Вот пример для установки фильтра: " +
                                "/region 01", menuMarkup);
            }

            String regionArgument = arguments.get(0);

            if (!regionArgument.matches("\\d{2}")) {
                return new CommandResponse(incomingMessageDto.getChatId(),
                        "Неправильно введены данные. Вот пример для установки фильтра: " +
                                "/region 01", menuMarkup);
            } else {
                filter.setRegion(regionArgument);
            }
            repositoryService.saveUser(user);
            return new CommandResponse(incomingMessageDto.getChatId(),
                    "Задайте критерии поиска и время нотификации.\n Затем нажмите \"Готово\", либо введите команду /ready\n"
                    , helper.getInlineKeyboardMarkup(filter));
        }

        String callbackData = incomingMessageDto.getCallbackData();
        int offset = 0;

        if (incomingMessageDto.getCallbackData().startsWith(REGION_PAGE)) {
            offset = Integer.parseInt(callbackData.replace(REGION_PAGE, ""));
        }

        if (incomingMessageDto.getCallbackData().startsWith(REGION_BUTTON)) {
            String regionCode = callbackData.replace(REGION_BUTTON, "");
            filter.setRegion(regionCode);
            repositoryService.saveUser(user);
            return new CommandResponse(incomingMessageDto.getChatId(),
                    "Задайте критерии поиска и время нотификации.\n Затем нажмите \"Готово\", либо введите команду /ready\n"
                    , helper.getInlineKeyboardMarkup(filter));
        }

        List<InlineKeyboardButton> buttons = createButton(regions);
        InlineKeyboardRow paginationRow = getInlineKeyboardButtons(offset, buttons);

        List<InlineKeyboardButton> onePageButtons = buttons.stream().skip(offset).limit(10).toList();
        List<InlineKeyboardRow> rows = row(onePageButtons);

        if (!paginationRow.isEmpty()) {
            rows.add(paginationRow);
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(rows);
        return new CommandResponse(incomingMessageDto.getChatId(), "Выбирайте регион:", markup);
    }

    @NotNull
    private static InlineKeyboardRow getInlineKeyboardButtons(int offset, List<InlineKeyboardButton> buttons) {
        InlineKeyboardRow paginationRow = new InlineKeyboardRow();

        if (offset > 0) {
            InlineKeyboardButton prev = new InlineKeyboardButton("Предыдущие 10");
            prev.setCallbackData(REGION_PAGE + (offset - 10));
            paginationRow.add(prev);
        }

        if (offset + 10 < buttons.size()) {
            InlineKeyboardButton next = new InlineKeyboardButton("Следующие 10");
            next.setCallbackData(REGION_PAGE + (offset + 10));
            paginationRow.add(next);
        }
        return paginationRow;
    }

    private List<InlineKeyboardButton> createButton(Map<String, String> map) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(key + " " + value);
            inlineKeyboardButton.setCallbackData(REGION_BUTTON + key);
            buttons.add(inlineKeyboardButton);
        }
        return buttons;
    }

    private List<InlineKeyboardRow> row(List<InlineKeyboardButton> buttons) {
        List<InlineKeyboardRow> rows = new ArrayList<>();
        for (InlineKeyboardButton button : buttons) {
            InlineKeyboardRow row = new InlineKeyboardRow(button);
            rows.add(row);
        }
        return rows;
    }
}
