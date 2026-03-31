package com.stepanio.java.training.telegram.app.adapter;

import com.stepanio.java.training.telegram.app.command.parser.CommandLineParser;
import com.stepanio.java.training.telegram.app.dto.CommandLineDto;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public class TelegramIncomingMessageAdapter {
    private final CommandLineParser commandLineParser;

    public TelegramIncomingMessageAdapter(CommandLineParser commandLineParser) {
        this.commandLineParser = commandLineParser;
    }

    public IncomingMessageDto map(Update update) {
        IncomingMessageDto incomingMessageDTO = new IncomingMessageDto();
        if (update != null && update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();

            incomingMessageDTO.setUserId(callbackQuery.getFrom().getId());
            incomingMessageDTO.setChatId(callbackQuery.getMessage().getChatId());
            incomingMessageDTO.setCallback(true);
            incomingMessageDTO.setCallbackData(callbackQuery.getData());
            incomingMessageDTO.setText(null);
            return incomingMessageDTO;
        }
        if (update != null && update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = update.getMessage().getText();
            CommandLineDto commandLine = commandLineParser.parse(text);
            incomingMessageDTO.setUserId(message.getFrom().getId());
            incomingMessageDTO.setChatId(message.getChatId());
            incomingMessageDTO.setCommandName(commandLine.getCommandName());
            incomingMessageDTO.setArguments(commandLine.getArguments());
            incomingMessageDTO.setText(text);
            incomingMessageDTO.setCallback(false);
            incomingMessageDTO.setCallbackData(null);
            return incomingMessageDTO;
        }
        incomingMessageDTO.setText("");
        return incomingMessageDTO;
    }
}

