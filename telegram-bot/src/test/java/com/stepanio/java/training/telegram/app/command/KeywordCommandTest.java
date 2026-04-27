package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.TestHelper;
import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.repository.RepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class KeywordCommandTest {
    private BotCommandHelper helper;
    private RepositoryService service;
    private KeywordCommand keywordCommand;
    private final TestHelper testHelper = new TestHelper();

    @BeforeEach
    void setUp () {
        helper = mock(BotCommandHelper.class);
        service = mock(RepositoryService.class);
        keywordCommand = new KeywordCommand(helper, service);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Java22", "123123", "Dev3loper", "123456789", ""})
    void handleTestWithInvalidArguments (String arguments) {
        BotUserDto user = testHelper.createUser();
        when(service.getUser(1L)).thenReturn(user);
        IncomingMessageDto message = testHelper.createMessage(arguments);
        CommandResponse response = keywordCommand.handle(message);
        assertNull(user.getFilter().getKeyword());
        assertEquals("Неправильно введены данные. Вот пример для установки фильтра: " +
                "/keyword Java", response.getText());
        verify(service, never()).saveUser(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Java", "Developer", "Software engineer"})
    void handleTestWithValidArguments (String arguments) {
        BotUserDto user = testHelper.createUser();
        when(service.getUser(1L)).thenReturn(user);
        IncomingMessageDto message = testHelper.createMessage(arguments);
        CommandResponse response = keywordCommand.handle(message);
        assertEquals(arguments, (user.getFilter().getKeyword()));
        verify(service).saveUser(user);
        assertEquals("Задайте критерии поиска и время нотификации.\n Затем нажмите \"Готово\", либо введите команду /ready\n"
                , response.getText());
    }

    @ParameterizedTest
    @ValueSource(strings = {"keyword_Java", "keyword_Developer", "keyword_Software engineer"})
    void handleTestWithSomeCallbackData (String callbackData) {
        BotUserDto user = testHelper.createUser();
        when(service.getUser(1L)).thenReturn(user);
        when(helper.getInlineKeyboardMarkup(any()))
                .thenReturn(testHelper.createMarkup());
        IncomingMessageDto message = new IncomingMessageDto();
        message.setCallback(true);
        message.setCallbackData(callbackData);
        message.setUserId(1L);
        message.setChatId(1L);
        CommandResponse response = keywordCommand.handle(message);
        String expected = callbackData.replace("keyword_", "");
        assertEquals(expected, user.getFilter().getKeyword());
        verify(service).saveUser(user);
        assertEquals(testHelper.createMarkup(), response.getMarkup());
        assertEquals("Задайте критерии поиска и время нотификации.\n Затем нажмите \"Готово\", либо введите команду /ready\n",
                response.getText());
    }

    @Test
    void handleTestWithoutArguments() {
        BotUserDto user = testHelper.createUser();
        when(service.getUser(1L)).thenReturn(user);

        IncomingMessageDto dto = new IncomingMessageDto();
        dto.setCommandName("/keyword");
        dto.setArguments(List.of());
        dto.setCallback(false);
        dto.setUserId(1L);
        dto.setChatId(1L);
        CommandResponse response = keywordCommand.handle(dto);
        assertTrue(response.getText().contains("Неправильно введены данные. Вот пример для установки фильтра: " +
                "/keyword Java"));
        assertNull(user.getFilter().getKeyword());
        verify(service, never()).saveUser(any());
    }


}
