package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.TestHelper;
import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.repository.RepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StartCommandTest {
    private RepositoryService service;
    private StartCommand startCommand;
    private TestHelper helper = new TestHelper();

    @BeforeEach
    void setUp () {
        service = mock(RepositoryService.class);
        startCommand = new StartCommand(service);
    }

    @Test
    void handleTestIfUserIsPresent () {
        BotUserDto user = helper.createUser();
        when(service.getUser(1L)).thenReturn(user);
        IncomingMessageDto message = new IncomingMessageDto();
        message.setUserId(1L);
        message.setChatId(1L);
        message.setCallback(false);
        CommandResponse response = startCommand.handle(message);
        verify(service, never()).saveUser(any());
        assertTrue(response.getText().contains("Vacancy Tracker Bot приветствует вас!\n" +
                "Вы ранее регистрировались в нашем боте, можете приступать к\nего использованию.\n" +
                "Сейчас используется часовой пояс UTC, для изменения введите,\nнапример UTC+3:30\n" +
                "\n" + "Ранее заданные фильтры:\n" + startCommand.getFilters(user.getFilter()) + "\n" + "\n" +
                "Введите команду /menu для получения списка команд"));
    }

    @Test
    void handleTestIfUserIsNull () {
        when(service.getUser(1L)).thenReturn(null);
        IncomingMessageDto message = new IncomingMessageDto();
        message.setUserId(1L);
        message.setChatId(1L);
        message.setCallback(false);
        CommandResponse response = startCommand.handle(message);
        verify(service).saveUser(any(BotUserDto.class));
        assertTrue(response.getText().contains("Вы успешно зарегистрировались в Vacancy Tracker Bot!\n" +
                "Будет использован часовой пояс UTC, для изменения введите\n" +
                "часовой пояс в формате UTC+3\n" +
                "Используйте команду /menu для получения списка команд"));
    }
}
