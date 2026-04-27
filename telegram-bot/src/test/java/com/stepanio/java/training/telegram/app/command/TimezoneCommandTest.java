package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.TestHelper;
import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.repository.RepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TimezoneCommandTest {
    private RepositoryService service;
    private TimezoneCommand timezoneCommand;
    private final TestHelper testHelper = new TestHelper();

    @BeforeEach
    void setUp () {
        service = mock(RepositoryService.class);
        timezoneCommand = new TimezoneCommand(service);
    }

    @Test
    void handleTestWithValidArguments () {
        BotUserDto user = testHelper.createUser();
        when(service.getUser(1L)).thenReturn(user);
        IncomingMessageDto message = new IncomingMessageDto();
        message.setCallback(false);
        message.setText("UTC+3");
        message.setUserId(1L);
        message.setChatId(1L);
        CommandResponse response = timezoneCommand.handle(message);
        assertEquals(ZoneId.of("UTC+3"), user.getTimezone());
        assertTrue(response.getText().contains("Часовой пояс установлен: UTC+3"));
    }
}
