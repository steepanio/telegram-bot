package com.stepanio.java.training.telegram.app.command.parser;

import com.stepanio.java.training.telegram.app.dto.CommandLineDto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

    public class CommandLineParser {
    public static final int COMMAND_NAME_PART = 0;
    public static final int COMMAND_ARGUMENTS_PART = 1;
    private static final String SLASH = "/";



    public CommandLineDto parse(String text) {
        if (!isCommandLine(text)) {
            return new CommandLineDto("", Collections.emptyList());
        }

        String trim = text.trim();
        if (!trim.startsWith(SLASH)) {
            return new CommandLineDto("", Collections.emptyList());
        }
        CommandLineDto commandLineDto = new CommandLineDto();

        String[] parts = trim.split("\\s+");
        String name = parts[COMMAND_NAME_PART];
        commandLineDto.setCommandName(name);
        List<String> args = (parts.length > COMMAND_ARGUMENTS_PART)
                ? Arrays.asList(parts).subList(1, parts.length)
                : Collections.emptyList();
        commandLineDto.setArguments(args);

        return commandLineDto;
    }

    private boolean isCommandLine(String text) {
        return text != null && !text.isBlank();
    }
}

