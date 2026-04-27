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
import static org.mockito.Mockito.*;

public class ExperienceCommandTest {
    private BotCommandHelper helper;
    private RepositoryService service;
    private ExperienceCommand experienceCommand;
    private final TestHelper testHelper = new TestHelper();

    @BeforeEach
    void setUp () {
        helper = mock(BotCommandHelper.class);
        service = mock(RepositoryService.class);
        experienceCommand = new ExperienceCommand(helper, service);
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "1a", "-5", "123456789", ""})
    void handleTestWithInvalidArguments (String arguments) {
        BotUserDto user = testHelper.createUser();
        when(service.getUser(1L)).thenReturn(user);
        IncomingMessageDto message = testHelper.createMessage(arguments);
        CommandResponse response = experienceCommand.handle(message);
        assertNull(user.getFilter().getExperience());
        assertEquals("Неправильно введены данные. Вот пример для установки фильтра:" +
                "/experience 5", response.getText());
        verify(service, never()).saveUser(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3", "4", "5"})
    void handleTestWithValidArguments (String arguments) {
        BotUserDto user = testHelper.createUser();
        when(service.getUser(1L)).thenReturn(user);
        IncomingMessageDto message = testHelper.createMessage(arguments);
        CommandResponse response = experienceCommand.handle(message);
        assertEquals(Integer.valueOf(arguments), (user.getFilter().getExperience()));
        verify(service).saveUser(user);
        assertEquals("Задайте критерии поиска и время нотификации.\n Затем нажмите \"Готово\", либо введите команду /ready\n"
        , response.getText());
    }

    @Test
    void handleTestWithCallbackDataSkip () {
        BotUserDto user = testHelper.createUser();
        when(service.getUser(1L)).thenReturn(user);
        when(helper.getInlineKeyboardMarkup(any()))
                .thenReturn(testHelper.createMarkup());
        IncomingMessageDto message = new IncomingMessageDto();
        message.setCallback(true);
        message.setCallbackData("experience_skip");
        message.setUserId(1L);
        message.setChatId(1L);
        CommandResponse response = experienceCommand.handle(message);
        assertNull(user.getFilter().getExperience());
        verify(service).saveUser(user);
        assertEquals(testHelper.createMarkup(), response.getMarkup());
        assertEquals("Задайте критерии поиска и время нотификации.\n Затем нажмите \"Готово\", либо введите команду /ready\n",
                response.getText());
    }

    @ParameterizedTest
    @ValueSource(strings = {"experience_1", "experience_3", "experience_5"})
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
        CommandResponse response = experienceCommand.handle(message);
        int expected = Integer.parseInt(callbackData.replace("experience_", ""));
        assertEquals(expected, user.getFilter().getExperience());
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
        dto.setCommandName("/experience");
        dto.setArguments(List.of());
        dto.setCallback(false);
        dto.setUserId(1L);
        dto.setChatId(1L);
        CommandResponse response = experienceCommand.handle(dto);
        assertTrue(response.getText().contains("Неправильно введены данные. Вот пример для установки фильтра:" +
                "/experience 5"));
        assertNull(user.getFilter().getExperience());
        verify(service, never()).saveUser(any());
    }
}
