package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.TestHelper;
import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnknownCommandTest {
    private UnknownCommand unknownCommand = new UnknownCommand();
    private TestHelper helper;

    @Test
    void handleTest () {
        IncomingMessageDto message = new IncomingMessageDto();
        CommandResponse response = unknownCommand.handle(message);
        assertEquals("Неизвестная команда! Нажмите /menu для вывода списка команд", response.getText());
    }
}
