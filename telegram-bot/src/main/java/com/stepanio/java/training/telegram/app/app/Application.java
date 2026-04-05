package com.stepanio.java.training.telegram.app.app;

import com.stepanio.java.training.telegram.app.adapter.TelegramIncomingMessageAdapter;
import com.stepanio.java.training.telegram.app.command.*;
import com.stepanio.java.training.telegram.app.command.dispatcher.CommandDispatcher;
import com.stepanio.java.training.telegram.app.command.parser.CommandLineParser;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.messageSender.MessageSenderImpl;
import com.stepanio.java.training.telegram.app.repository.CsvRegionsRepository;
import com.stepanio.java.training.telegram.app.repository.JsonUserRepository;
import com.stepanio.java.training.telegram.app.repository.JsonVacancyRepository;
import com.stepanio.java.training.telegram.app.scheduler.TaskScheduler;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.stepan.trudvsem.client.http.TrudVsemRestClient;

import java.util.List;

public class Application implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramIncomingMessageAdapter adapter;
    private final CommandDispatcher dispatcher;
    private final MessageSenderImpl sender;
    private final TelegramBotsLongPollingApplication telegramApp;
    private final String botToken;

    public Application(
            TelegramIncomingMessageAdapter adapter,
            CommandDispatcher dispatcher,
            MessageSenderImpl sender,
            TelegramBotsLongPollingApplication telegramApp, String botToken
    ) {
        this.adapter = adapter;
        this.dispatcher = dispatcher;
        this.sender = sender;
        this.telegramApp = telegramApp;
        this.botToken = botToken;
    }

    public void run() throws TelegramApiException {
        telegramApp.registerBot(botToken, this);
    }

    @Override
    public void consume(Update update) {
        IncomingMessageDto dto = adapter.map(update);
        CommandResponse response = dispatcher.dispatch(dto);

        if (!(response == null)) {
            sender.sendText(response.getChatId(), response.getText(), response.getMarkup());
        }
    }
}


