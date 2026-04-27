package com.stepanio.java.training.telegram.app.adapter;

import com.stepanio.java.training.telegram.app.command.parser.CommandLineParser;
import com.stepanio.java.training.telegram.app.dto.CommandLineDto;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TelegramIncomingMessageAdapterTest {
    @Mock
    private CommandLineParser commandLineParser;

    @Mock
    private Update update;

    @Mock
    private User user;

    @Mock
    private Message message;

    @Mock
    private CallbackQuery callbackQuery;

    private TelegramIncomingMessageAdapter telegramIncomingMessageAdapter;

    @BeforeEach
    void setUp() {
        telegramIncomingMessageAdapter = new TelegramIncomingMessageAdapter(commandLineParser);
    }

    @Test
    void mapTestWIthText() {
        String text = "/salary 25000 75000";

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getFrom()).thenReturn(user);
        when(message.getText()).thenReturn(text);
        when(user.getId()).thenReturn(123L);
        when(message.getChatId()).thenReturn(456L);
        when(commandLineParser.parse(text)).thenReturn(new CommandLineDto("/salary", List.of("25000", "75000")));

        IncomingMessageDto result = telegramIncomingMessageAdapter.map(update);

        assertEquals(123L, result.getUserId());
        assertEquals(456L, result.getChatId());
        assertEquals(text, result.getText());
        assertEquals("/salary", result.getCommandName());
        assertEquals(List.of("25000", "75000"), result.getArguments());
        assertFalse(result.isCallback());
        assertNull(result.getCallbackData());

        verify(commandLineParser).parse(text);
    }

    @Test
    void mapTestWithNull() {
        IncomingMessageDto result = telegramIncomingMessageAdapter.map(null);
        assertEquals("", result.getText());
    }

    @Test
    void mapTestWithoutText() {
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(false);

        IncomingMessageDto result = telegramIncomingMessageAdapter.map(update);

        assertEquals("", result.getText());
    }

    @Test
    void mapTestWithCallback () {
        when(update.hasCallbackQuery()).thenReturn(true);
        when(update.getCallbackQuery()).thenReturn(callbackQuery);
        when(callbackQuery.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(123L);
        when(callbackQuery.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(456L);
        when(message.getMessageId()).thenReturn(789);

        when(callbackQuery.getData()).thenReturn("some_data");

        IncomingMessageDto result = telegramIncomingMessageAdapter.map(update);

        assertTrue(result.isCallback());
        assertEquals("some_data", result.getCallbackData());
        assertNull(result.getText());

        assertEquals(123L, result.getUserId());
        assertEquals(456L, result.getChatId());
        assertEquals(789, result.getMessageId());

    }
}
