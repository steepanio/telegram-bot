import com.stepanio.java.training.telegram.app.command.CommandDispatcher;
import com.stepanio.java.training.telegram.app.command.commands.BotCommand;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

import static org.mockito.Mockito.*;

public class CommandDispatcherTest {


    @Test
    void dispatchTest() {
        BotCommand startCommand = mock(BotCommand.class);
        BotCommand otherCommand = mock(BotCommand.class);
        BotCommand defaultCommand = mock(BotCommand.class);

        IncomingMessageDto incomingMessageDto = new IncomingMessageDto();
        incomingMessageDto.setCommandName("/unknown");

        when(startCommand.supports(incomingMessageDto)).thenReturn(false);
        when(otherCommand.supports(incomingMessageDto)).thenReturn(false);
        when(defaultCommand.handle(incomingMessageDto)).thenReturn(new SendMessage("123", "default"));

        CommandDispatcher commandDispatcher = new CommandDispatcher(List.of(startCommand, otherCommand), defaultCommand);
        commandDispatcher.dispatch(incomingMessageDto);

        verify(startCommand, never()).handle(incomingMessageDto);
        verify(otherCommand, never()).handle(incomingMessageDto);
        verify(defaultCommand, times(1)).handle(incomingMessageDto);
    }
}
