package com.stepanio.java.training.telegram.app.command.dispatcher;

import com.stepanio.java.training.telegram.app.command.BotCommand;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class CommandDispatcherTest {


    @Test
    void dispatchTest() {
        BotCommand startCommand = mock(BotCommand.class);
        BotCommand otherCommand = mock(BotCommand.class);
        BotCommand unknownCommand = mock(BotCommand.class);

        IncomingMessageDto incomingMessageDto = new IncomingMessageDto();
        incomingMessageDto.setCommandName("/unknown");

        when(startCommand.supports(incomingMessageDto)).thenReturn(false);
        when(otherCommand.supports(incomingMessageDto)).thenReturn(false);
        when(unknownCommand.handle(incomingMessageDto)).thenReturn(new CommandResponse(123L, "default", null));

        CommandDispatcher commandDispatcher = new CommandDispatcher(List.of(startCommand, otherCommand), unknownCommand);
        commandDispatcher.dispatch(incomingMessageDto);

        verify(startCommand, never()).handle(incomingMessageDto);
        verify(otherCommand, never()).handle(incomingMessageDto);
        verify(unknownCommand, times(1)).handle(incomingMessageDto);
    }
}
