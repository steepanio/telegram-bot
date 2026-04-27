import com.stepanio.java.training.telegram.app.command.parser.CommandLineParser;
import com.stepanio.java.training.telegram.app.dto.CommandLineDto;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandLineParserTest {
    private IncomingMessageDto incomingMessageDto = new IncomingMessageDto();

    public static final String PREFIX = "/";
    private CommandLineParser commandLineParser = new CommandLineParser();

    @ParameterizedTest
    @MethodSource("commandData")
    void commandLineParserTest(String s, CommandLineDto expected) {
        CommandLineDto commandLine = commandLineParser.parse(s);
        assertEquals(expected, commandLine);
    }

    static Stream<Arguments> commandData() {
        return Stream.of(Arguments.of("/salary", new CommandLineDto("/salary", Collections.emptyList())),
                Arguments.of("/salary 25000 75000", new CommandLineDto("/salary", List.of("25000", "75000"))),
                Arguments.of("salary 25000", new CommandLineDto("", Collections.emptyList())),
                Arguments.of(null, new CommandLineDto("", Collections.emptyList())),
                Arguments.of("  /salary 25000  ", new CommandLineDto("/salary", List.of("25000")))
        );
    }
}

