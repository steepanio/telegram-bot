package com.stepanio.java.training.telegram.app.app;

import com.stepanio.java.training.telegram.app.adapter.TelegramIncomingMessageAdapter;
import com.stepanio.java.training.telegram.app.command.dispatcher.CommandDispatcher;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.messageSender.MessageSenderImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.Mockito.*;

public class ApplicationTest {
    private TelegramIncomingMessageAdapter adapter;
    private CommandDispatcher dispatcher;
    private MessageSenderImpl sender;
    private Update update;
    private IncomingMessageDto message = new IncomingMessageDto();
    private CommandResponse response = new CommandResponse(1L, "text", null);
    private Application application;
    private TelegramBotsLongPollingApplication telegramApp;

    @BeforeEach
    void setUp () {
        adapter = mock(TelegramIncomingMessageAdapter.class);
        dispatcher = mock(CommandDispatcher.class);
        sender = mock(MessageSenderImpl.class);
        update = mock(Update.class);
        telegramApp = mock(TelegramBotsLongPollingApplication.class);

        application = new Application(
                adapter,
                dispatcher,
                sender,
                telegramApp,
                "test-token"
        );
    }

    @Test
    void consumeTest () {
        when(adapter.map(update)).thenReturn(message);
        when(dispatcher.dispatch(message)).thenReturn(response);

        application.consume(update);

        verify(adapter).map(update);
        verify(dispatcher).dispatch(message);
        verify(sender).sendText(1L, "text", null);
    }

    @Test
    void consumeTestIfResponseIsNull () {
        when(adapter.map(update)).thenReturn(message);
        when(dispatcher.dispatch(message)).thenReturn(null);

        application.consume(update);

        verify(sender, never()).sendText(1L, "text", null);
    }

    @Test
    void consumeTestIfTextIsNull() {
        CommandResponse responseNull = new CommandResponse(1L, null, null);
        when(adapter.map(update)).thenReturn(message);
        when(dispatcher.dispatch(message)).thenReturn(responseNull);

        application.consume(update);

        verify(sender, never()).sendText(1L, null, null);
    }
}
