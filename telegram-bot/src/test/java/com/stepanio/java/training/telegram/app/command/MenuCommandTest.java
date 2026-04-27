package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.TestHelper;
import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.repository.RepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MenuCommandTest {
    private BotCommandHelper helper;
    private RepositoryService service;
    private MenuCommand menuCommand;
    private final TestHelper testHelper = new TestHelper();

    @BeforeEach
    void setUp () {
        helper = mock(BotCommandHelper.class);
        service = mock(RepositoryService.class);
        menuCommand = new MenuCommand(helper, service);
    }

    @Test
    void handleTestWithFilter () {
        BotUserDto user = testHelper.createUser();
        when(service.getUser(1L)).thenReturn(user);
        when(helper.getInlineKeyboardMarkup(any()))
                .thenReturn(testHelper.createMarkup());
        IncomingMessageDto message = testHelper.createMessage("");
        CommandResponse response = menuCommand.handle(message);
        assertEquals("Задайте критерии поиска и время нотификации.\n Затем нажмите \"Готово\", либо введите команду /ready\n",
                response.getText());
        assertEquals(testHelper.createMarkup(), response.getMarkup());
    }

    @Test
    void handleTestWithoutFilter () {
        BotUserDto user = testHelper.createUser();
        user.setFilter(null);
        when(service.getUser(1L)).thenReturn(user);
        when(helper.getInlineKeyboardMarkup(any()))
                .thenReturn(testHelper.createMarkup());
        IncomingMessageDto message = testHelper.createMessage("");
        CommandResponse response = menuCommand.handle(message);
        assertEquals("Задайте критерии поиска и время нотификации.\n Затем нажмите \"Готово\", либо введите команду /ready\n",
                response.getText());
        assertEquals(testHelper.createMarkup(), response.getMarkup());
        assertNotNull(user.getFilter());
    }
}
