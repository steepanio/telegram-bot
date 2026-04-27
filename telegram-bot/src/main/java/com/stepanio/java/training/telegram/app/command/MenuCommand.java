package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.dto.SearchFilter;
import com.stepanio.java.training.telegram.app.repository.RepositoryService;

public class MenuCommand implements BotCommand {
    private final BotCommandHelper helper;
    private final RepositoryService repositoryService;

    public MenuCommand(BotCommandHelper helper, RepositoryService repositoryService) {
        this.helper = helper;
        this.repositoryService = repositoryService;
    }

    @Override
    public boolean supports(IncomingMessageDto incomingMessageDto) {
        return BotCommandConstants.MENU_COMMAND.equals(incomingMessageDto.getCommandName())
                || incomingMessageDto.isCallback()
                && BotCommandConstants.MENU_CALLBACK_DATA.equals(incomingMessageDto.getCallbackData());
    }

    @Override
    public CommandResponse handle(IncomingMessageDto incomingMessageDto) {
        BotUserDto user = repositoryService.getUser(incomingMessageDto.getUserId());
        SearchFilter filter = user.getFilter();
        if (filter == null) {
            user.setFilter(new SearchFilter());
            filter = user.getFilter();
        }
        String menu = "Задайте критерии поиска и время нотификации.\n Затем нажмите \"Готово\", либо введите команду /ready\n";
        return new CommandResponse(incomingMessageDto.getChatId(), menu, helper.getInlineKeyboardMarkup(filter));
    }


}

