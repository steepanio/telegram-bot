package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.repository.CsvRegionsRepository;
import com.stepanio.java.training.telegram.app.repository.JsonUserRepository;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegionCommand implements BotCommand {
    private final JsonUserRepository userRepository;
    private final CsvRegionsRepository regionsRepository;
    private static final String REGION_BUTTON = "region_";
    private static final String REGION_PAGE = "region_page_";


    public RegionCommand(JsonUserRepository userRepository, CsvRegionsRepository regionsRepository) {
        this.userRepository = userRepository;
        this.regionsRepository = regionsRepository;
    }

    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return BotCommandConstants.REGION_COMMAND.equals(incomingMessageDto.getCommandName())
                || BotCommandConstants.REGION_CALLBACK_DATA.equals(incomingMessageDto.getCallbackData())
                || incomingMessageDto.getCallbackData().startsWith(REGION_BUTTON)
                || incomingMessageDto.getCallbackData().startsWith(REGION_PAGE);
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        BotUserDto user = userRepository.findById(incomingMessageDto.getUserId()).orElseThrow();

        if (!incomingMessageDto.isCallback()) {
            user.getFilter().setRegion((incomingMessageDto.getArguments().get(0)));
            userRepository.save(user);
            return new CommandResponse(incomingMessageDto.getChatId(), null, null);
        }

        Map<String, String> regions = regionsRepository.readRows("/russian_regions_codes.csv");
        String callbackData = incomingMessageDto.getCallbackData();
        int offset = 0;

        if (incomingMessageDto.isCallback()) {
            if (incomingMessageDto.getCallbackData().startsWith(REGION_PAGE)) {
                offset = Integer.parseInt(callbackData.replace(REGION_PAGE, ""));
            }

            if (incomingMessageDto.getCallbackData().startsWith(REGION_BUTTON)) {
                String regionCode = callbackData.replace(REGION_BUTTON, "");
                user.getFilter().setRegion(regionCode);
                userRepository.save(user);
                return new CommandResponse(incomingMessageDto.getChatId(), null, null);
            }
        }

        List<InlineKeyboardButton> buttons = createButton(regions);
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

        List<InlineKeyboardButton> onePageButtons = buttons.stream().skip(offset).limit(10).toList();
        List<InlineKeyboardRow> rows = row(onePageButtons);

        if (!paginationRow.isEmpty()) {
            rows.add(paginationRow);
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(rows);
        return new CommandResponse(incomingMessageDto.getChatId(), "Выбирайте регион:", markup);
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
