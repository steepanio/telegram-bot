package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.TestHelper;
import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.repository.CsvRegionsRepository;
import com.stepanio.java.training.telegram.app.repository.RepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

public class RegionCommandTest {
    private BotCommandHelper helper;
    private RepositoryService service;
    private RegionCommand regionCommand;
    private final TestHelper testHelper = new TestHelper();

    @BeforeEach
    void setUp () {
        CsvRegionsRepository csvRegionsRepository = mock(CsvRegionsRepository.class);
        helper = mock(BotCommandHelper.class);
        service = mock(RepositoryService.class);
        regionCommand = new RegionCommand(csvRegionsRepository, helper, service);
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "1a", "-50", "123456789", ""})
    void handleTestWithInvalidArguments (String arguments) {
        BotUserDto user = testHelper.createUser();
        when(service.getUser(1L)).thenReturn(user);
        IncomingMessageDto message = testHelper.createMessage(arguments);
        CommandResponse response = regionCommand.handle(message);
        assertNull(user.getFilter().getRegion());
        assertEquals("Неправильно введены данные. Вот пример для установки фильтра: " +
                "/region 01", response.getText());
        verify(service, never()).saveUser(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"10", "20", "30", "40", "50"})
    void handleTestWithValidArguments (String arguments) {
        BotUserDto user = testHelper.createUser();
        when(service.getUser(1L)).thenReturn(user);
        IncomingMessageDto message = testHelper.createMessage(arguments);
        CommandResponse response = regionCommand.handle(message);
        assertEquals(arguments, user.getFilter().getRegion());
        verify(service).saveUser(user);
        assertEquals("Задайте критерии поиска и время нотификации.\n Затем нажмите \"Готово\", либо введите команду /ready\n"
                , response.getText());
    }

    @ParameterizedTest
    @ValueSource(strings = {"region_01", "region_30", "region_50"})
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
        CommandResponse response = regionCommand.handle(message);
        String expected = callbackData.replace("region_", "");
        assertEquals(expected, user.getFilter().getRegion());
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
        dto.setCommandName("/region");
        dto.setArguments(List.of());
        dto.setCallback(false);
        dto.setUserId(1L);
        dto.setChatId(1L);
        CommandResponse response = regionCommand.handle(dto);
        assertTrue(response.getText().contains("Неправильно введены данные. Вот пример для установки фильтра: " +
                "/region 01"));
        assertNull(user.getFilter().getRegion());
        verify(service, never()).saveUser(any());
    }

    @Test
    void handleTestWithPagesCallbackData () {
        BotUserDto user = testHelper.createUser();
        when(service.getUser(1L)).thenReturn(user);
        IncomingMessageDto message = new IncomingMessageDto();
        message.setCallback(true);
        message.setCallbackData("page_region20");
        message.setUserId(1L);
        message.setChatId(1L);
        CommandResponse response = regionCommand.handle(message);
        verify(service, never()).saveUser(any());
        assertNull(user.getFilter().getRegion());
        assertTrue(response.getText().contains("Выбирайте регион:"));
        assertNotNull(response.getMarkup());
    }
}
